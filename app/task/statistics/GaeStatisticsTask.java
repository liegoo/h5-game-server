package task.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import common.task.Task;
import jws.Logger;
import utils.DateUtil;

/**
 * 
 * 定时按天统计抢红包运营数据
 */
public class GaeStatisticsTask extends Task {

	@Override
	public void run() {
		List<String> needStatisticDay = DollStatisticsTask.getStatisticDate("gae_statistics");
		if(needStatisticDay.size() == 0){
			return;
		}
		for(String date : needStatisticDay){
			Logger.info("统计抢红包数据:"+date);
			
			try{
				List<Record> rec2 = getRec2(date);
				if(rec2.size() != 4){
					Logger.warn("抢红包场次有调整，统计服务无法执行");
					return;
				}
				Record rec1 = getRec1(date);
				StringBuffer sql = new StringBuffer();
				sql.append("INSERT INTO gae_statistics(statistics_date,play_num,order_num_500,join_num_500,order_num_1000,join_num_1000,");
				sql.append("order_num_5000,join_num_5000,order_num_10000,join_num_10000,putin_beans,no_deduct_putin_beans,dc_beans,return_rate,win_rate,create_time) ");
				sql.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now())");
				List<Object> params = new ArrayList<Object>();
				params.add(date);
				params.add(rec1.get("play_num") == null ? 0 : rec1.get("play_num"));
				for(Record r : rec2){
					params.add(r.get("order_num"));
					params.add(r.get("person_num"));
				}
				int putinBeans = rec1.get("putin_beans") == null ? 0 : Integer.parseInt(rec1.get("putin_beans").toString());
				params.add(putinBeans);
				int noDeductPutinBeans = (int) (putinBeans/1.1);
				params.add(noDeductPutinBeans);
				int dcBeans = rec1.get("dc_beans")== null ? 0 : Integer.parseInt(rec1.get("dc_beans").toString());
				params.add(dcBeans);
				
				double returnRate = 0;
				if(noDeductPutinBeans != 0){
					returnRate = dcBeans*100.0/noDeductPutinBeans;
					BigDecimal bg = new BigDecimal(returnRate).setScale(3, RoundingMode.HALF_UP);
					returnRate = bg.doubleValue();
				}
				params.add(returnRate);
				params.add(rec1.get("win_rate")== null ? 0 : rec1.get("win_rate"));
				Db.update(sql.toString(), params.toArray());
			}catch (Exception e) {
				Logger.warn("没有成功插入抢红包数据（"+e.getMessage()+"），可能是其他机器的定时器已做了统计");
			}
		}
		
	}
	
	/**
	 * 获取玩游戏人数、投入开心豆、兑成开心豆、中奖率
	 * @return
	 */
	private Record getRec1(String statisticDay){
		StringBuffer sql = new StringBuffer();
		sql.append("select sum(deduct_beans) putin_beans,sum(hit_beans) dc_beans,ROUND(sum(IF(is_worst=1,0,1))/count(*)*100, 3) win_rate ");
		sql.append("from gae_draw_record where is_robot=0 and date(hit_time) = ? ");
		Record rec = Db.findFirst(sql.toString(), statisticDay);
		
		Record playNum = Db.findFirst("select count(DISTINCT uid) play_num from member_log where date(op_time)=? and game_id=1000002 ", statisticDay);
		rec.set("play_num", playNum.get("play_num"));
		return rec;
	}
	
	/**
	 * 获取订单数、人数
	 * @return
	 * 注意：此处的数据依赖场次豆多少（排序），场次个数，如果有变动，需要修改统计表结构
	 */
	private List<Record> getRec2(String statisticDay){
		StringBuffer sql = new StringBuffer();
		sql.append("select count(gdr.id) order_num,count(DISTINCT gdr.user_id) person_num ");
		sql.append("from gae_room groo left join gae_draw_record gdr ON (gdr.is_robot=0 and date(gdr.hit_time) = ? and gdr.room_id=groo.id) ");
		sql.append("group by groo.id ORDER BY groo.total_beans ");
//		sql.append("select count(*) order_num,count(DISTINCT gdr.user_id) person_num ");
//		sql.append("from gae_draw_record gdr join gae_room groo ON (gdr.room_id=groo.id) ");
//		sql.append("where gdr.is_robot=0 and date(gdr.hit_time) = date_sub(curdate(),interval 1 day) group by gdr.room_id ORDER BY groo.total_beans ");
		return Db.find(sql.toString(), statisticDay);
	}
	
}
