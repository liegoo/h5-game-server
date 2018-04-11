package moudles.gae.service.child;
import java.util.Date;
import java.util.List;

import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import jws.module.constants.gae.GaeAllConstans;
import jws.utils.IDGenerator;
import moudles.gae.assist.UnfitResultException;
import moudles.gae.ddl.GaeDrawDDL;
import moudles.gae.ddl.GaeDrawTempDDL;
import moudles.gae.ddl.GaeRoomDDL;

import org.apache.commons.lang.StringUtils;

import utils.DaoUtil;
import utils.IdGenerator2;


/**
 * 抢红包 -- 期数 service
 * 
 * @author caixb
 *
 */
public class GaeDrawService{
	
	/**
	 * 查询当前正在进行的抽奖活动
	 * 
	 * @param roomId
	 * @return
	 */
	public static GaeDrawTempDDL getActiveDraw(String roomId) {
		if(StringUtils.isBlank(roomId)){
			return null;
		}
		Condition cond = new Condition("GaeDrawTempDDL.roomId", "=", roomId);
		List<GaeDrawTempDDL> gaeDraws = Dal.select(DaoUtil.genAllFields(GaeDrawTempDDL.class), cond, null, 0, -1);
		if(gaeDraws != null && gaeDraws.size() != 0){
			return gaeDraws.get(0);
		}
		return null;
	}
	
	/**
	 * 根据期数id查询抽奖活动
	 * 
	 * @param status
	 * @return
	 */
	public static GaeDrawDDL getDraw(String drawId) {
		if(StringUtils.isBlank(drawId)){
			return null;
		}
		Condition cond = new Condition("GaeDrawDDL.id", "=", drawId);
		List<GaeDrawDDL> gaeDraws = Dal.select(DaoUtil.genAllFields(GaeDrawDDL.class), cond, new Sort("GaeDrawDDL.createTime", true), 0, -1);
		if(gaeDraws != null && gaeDraws.size() != 0){
			return gaeDraws.get(0);
		}
		return null;
	}
	
	/**
	 * 完成开奖
	 * 
	 * @param status
	 * @return
	 */
	public static boolean drawOver(String drawId) {
		if(StringUtils.isBlank(drawId)){
			return false;
		}
		Condition cond = new Condition("GaeDrawDDL.id", "=", drawId);
		cond.add(new Condition("GaeDrawDDL.status","=",GaeAllConstans.DRAW_STATUS_WAIT), "AND");
		GaeDrawDDL draw = new GaeDrawDDL();
		draw.setStatus(GaeAllConstans.DRAW_STATUS_OVER);
		draw.setDrawTime(new Date().getTime());
		int result = Dal.update(draw, "GaeDrawDDL.status, GaeDrawDDL.drawTime", cond);
		return result == 1;
	}
	
	/**
	 * 创建新的一期
	 * 
	 * @param roomId　　房间id
	 * @param lastDrawId 上一场期数id
	 * @return
	 */
	public static boolean createNewDraw(String roomId, String lastDrawId){
		GaeRoomDDL room = GaeRoomService.getRoom(roomId);
		if(room == null){
			Logger.debug("创建新的抢红包，创建失败, 红包场次不存在; roomId:%s", roomId);
			return false;
		}
		if(room.getStatus() != GaeAllConstans.ROOM_STATUS_VALID){
			Logger.debug("创建新的抢红包，创建失败, 红包场次无效; roomId:%s;status:%s", roomId, room.getStatus());
			Condition con = new Condition("GaeDrawTempDDL.roomId", "=", roomId);
			Dal.delete(con); 
			return false;
		}
		GaeDrawTempDDL drawTemp = getActiveDraw(roomId);
		
		if(drawTemp == null){
			//创建第一期
			try {
				Dal.beginTransaction(); 
				//创建新的关联关系
				String newDrawId = String.valueOf(IDGenerator.getId());
				GaeDrawTempDDL thisDrawTemp = new GaeDrawTempDDL();
				thisDrawTemp.setDrawId(newDrawId);
				thisDrawTemp.setRoomId(roomId);
				thisDrawTemp.setLastDrawId("--");
				thisDrawTemp.setRoomName(room.getName());
				thisDrawTemp.setHeadCount(room.getHeadCount());
				thisDrawTemp.setCeateTime(System.currentTimeMillis());
				
				int insResult = Dal.insert(thisDrawTemp);
				if(insResult != 1){
					throw new UnfitResultException("创建新的关联新旧开奖失败");
				}
				//创建新的一期开奖
				GaeDrawDDL newGaeDraw = new GaeDrawDDL(); 
				newGaeDraw.setCreateTime(System.currentTimeMillis());
				newGaeDraw.setHeadCount(room.getHeadCount());
				newGaeDraw.setId(newDrawId);
				newGaeDraw.setRoomDrawRatio(room.getDrawRatio());
				newGaeDraw.setRoomDrawSettings(room.getDrawSettings());
				newGaeDraw.setRoomId(roomId);
				newGaeDraw.setRoomName(room.getName());
				newGaeDraw.setRoomTotalBeans(room.getTotalBeans());
				newGaeDraw.setStatus(GaeAllConstans.DRAW_STATUS_WAIT);
				newGaeDraw.setThisHeadCount(0);
				int insertResult = Dal.insert(newGaeDraw);
				if(insertResult != 1){
					throw new UnfitResultException("创建新的一期开奖失败");
				}
				Dal.setTransactionSuccessful();
				return true;
			} catch (UnfitResultException ur) {
				Logger.debug(ur.getMessage());
				return false;
			} catch (Exception e) {
				Logger.info("创建新的一期开奖失败：Exception：" + e);
				return false;
			}finally{
				Dal.endTransaction();
			}
		}else{
			try {
				Dal.beginTransaction(); 
				if(StringUtils.isBlank(lastDrawId)){
					Logger.debug("创建新的抢红包，创建失败, 上一期id为空; roomId:%s;lastDrawId:%s", roomId, lastDrawId);
					return false;
				}
				GaeDrawDDL draw = GaeDrawService.getDraw(lastDrawId);
				if(draw.getStatus() != GaeAllConstans.DRAW_STATUS_OVER){
					Logger.debug("创建新的抢红包，创建失败,上一期未结束; drawId:%s;status:%s", lastDrawId, draw.getStatus());
					return false;
				}

				GaeDrawDDL thisDraw = GaeDrawService.getDraw(drawTemp.getDrawId());
				if(thisDraw.getStatus() != GaeAllConstans.DRAW_STATUS_OVER){
					Logger.debug("创建新的抢红包，创建失败,当前正在进行一期未结束; thisDrawId:%s;status:%s", thisDraw.getId(), thisDraw.getStatus());
					return false;
				}
				
				//关联新旧开奖
				String newDrawId = String.valueOf(IDGenerator.getId());
				GaeDrawTempDDL thisDrawTemp = new GaeDrawTempDDL();
				thisDrawTemp.setDrawId(newDrawId);
				thisDrawTemp.setRoomId(roomId);
				thisDrawTemp.setLastDrawId(lastDrawId);
				thisDrawTemp.setRoomName(room.getName());
				thisDrawTemp.setHeadCount(room.getHeadCount());
				
				Condition cond = new Condition("GaeDrawTempDDL.id", "=", drawTemp.getId());
				cond.add(new Condition("GaeDrawTempDDL.roomId", "=", roomId), "AND");
				cond.add(new Condition("GaeDrawTempDDL.roomId", "=", roomId), "AND");
				int updateResult = Dal.update(thisDrawTemp, "GaeDrawTempDDL.drawId,GaeDrawTempDDL.roomId,GaeDrawTempDDL.lastDrawId,GaeDrawTempDDL.roomName,GaeDrawTempDDL.headCount", cond);
				if(updateResult != 1){
					throw new UnfitResultException("关联新旧开奖失败");
				}
				//创建新的一期开奖
				GaeDrawDDL newGaeDraw = new GaeDrawDDL(); 
				newGaeDraw.setCreateTime(System.currentTimeMillis());
				newGaeDraw.setHeadCount(room.getHeadCount());
				newGaeDraw.setId(newDrawId);
				newGaeDraw.setRoomDrawRatio(room.getDrawRatio());
				newGaeDraw.setRoomDrawSettings(room.getDrawSettings());
				newGaeDraw.setRoomId(roomId);
				newGaeDraw.setRoomName(room.getName());
				newGaeDraw.setRoomTotalBeans(room.getTotalBeans());
				newGaeDraw.setStatus(GaeAllConstans.DRAW_STATUS_WAIT);
				newGaeDraw.setThisHeadCount(0);
				int insertResult = Dal.insert(newGaeDraw);
				if(insertResult != 1){
					throw new UnfitResultException("创建新的一期开奖失败");
				}
				Dal.setTransactionSuccessful();
				return true;
			} catch (UnfitResultException ur) {
				Logger.debug(ur.getMessage());
				return false;
			} catch (Exception e) {
				Logger.info("创建新的一期开奖失败：Exception：" + e);
				return false;
			}finally{
				Dal.endTransaction();
			}
		}
	}
	
}
