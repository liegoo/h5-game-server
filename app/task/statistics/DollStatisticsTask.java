package task.statistics;

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
 * 定时按天统计夹娃娃运营数据
 */
public class DollStatisticsTask extends Task {

	@Override
	public void run() {
		List<String> needStatisticDay = getStatisticDate("doll_statistics");
		if(needStatisticDay.size() == 0){
			return;
		}
		for(String date : needStatisticDay){
			Logger.info("统计夹娃娃数据:"+date);
			try{
				List<Record> rec2 = getRec2(date);
				if(rec2.size() != 4){
					Logger.warn("夹娃娃场次有调整，统计服务无法执行");
					continue;
				}
				Record rec1 = getRec1(date);
				Record rec3 = getRec3(date);
				StringBuffer sql = new StringBuffer();
				sql.append("INSERT INTO doll_statistics(statistics_date,play_num,order_num_1,join_num_1,order_num_2,join_num_2,order_num_3,join_num_3,order_num_4,join_num_4,putin_beans,dc_beans,win_rate,create_time)");
				sql.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,now())");
				List<Object> params = new ArrayList<Object>();
				params.add(date);
				params.add(rec1.get("play_num") == null ? 0 : rec1.get("play_num"));
				for(Record r : rec2){
					params.add(r == null ? 0 : r.get("order_num"));
					params.add(r == null ? 0 : r.get("person_num"));
				}
				params.add(rec1.get("putin_beans") == null ? 0 : rec1.get("putin_beans"));
				params.add(rec3.get("dc_beans") == null ? 0 : rec3.get("dc_beans"));
				params.add(rec1.get("win_rate") == null ? 0 : rec1.get("win_rate"));
				Db.update(sql.toString(), params.toArray());
			}catch (Exception e) {
				e.printStackTrace();
				Logger.info("没有成功插入夹娃娃数据（"+e.getMessage()+"），可能是其他机器的定时器已做了统计");
			}
		}
	}
	
	/**
	 * 获取玩游戏人数、投入开心豆、中奖率
	 * @return
	 */
	private Record getRec1(String statisticDay){
		StringBuffer sql = new StringBuffer();
		sql.append("select sum(happy_bean) putin_beans,sum(if(hit=1,1,0))/count(*)*100 win_rate ");
		sql.append("from award_record_doll where date(create_time) = ? ");
		Record rec = Db.findFirst(sql.toString(), statisticDay);
		
		Record playNum = Db.findFirst("select count(DISTINCT uid) play_num from member_log where date(op_time)=? and game_id=1000000 ", statisticDay);
		rec.set("play_num", playNum.get("play_num"));
		return rec;
	}
	
	/**
	 * 各个场次的订单数、人数
	 */
	private List<Record> getRec2(String statisticDay){
		StringBuffer sql = new StringBuffer();
		sql.append("select game_level,count(*) order_num,count(DISTINCT uid) person_num from award_record_doll ");
		sql.append("where date(create_time) = ? GROUP BY game_level ORDER BY game_level ");
		List<Record> recs = Db.find(sql.toString(), statisticDay);
		if(recs.size() == 4){
			return recs;
		}
		List<Record> temp = new ArrayList<Record>();
		temp.add(null);
		temp.add(null);
		temp.add(null);
		temp.add(null);
		for(Record r : recs){
			int level = r.getInt("game_level");
			if(level <= temp.size()){
				temp.set(level-1, r);
			}
		}
		return temp;
	}
	
	/**
	 * 获取兑成开心豆
	 * @return
	 */
	private Record getRec3(String statisticDay){
		StringBuffer sql = new StringBuffer();
		sql.append("select sum(happy_bean) dc_beans from member_log ");
		sql.append("where game_id=1000000 and op_type = 100 and date(op_time) = ? ");
		return Db.findFirst(sql.toString(), statisticDay);
	}
	
	/**
	 * 获取需要统计的日期（扫描最近15天）
	 * @return
	 */
	public static List<String> getStatisticDate(String tableName){
		List<String> needStatistic = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = DateUtil.addDay(new Date(), -15);
		String startDateStr = sdf.format(startDate);
		List<Record> statisticedDay = null;
		try{
			statisticedDay = Db.find("select statistics_date from "+tableName+" where statistics_date >= ?", startDateStr);
		}catch(Exception e){
			e.printStackTrace();
			return new ArrayList<String>();
		}
		List<String> statisticedDayList = new ArrayList<String>();
		for(Record rec : statisticedDay){
			statisticedDayList.add(rec.get("statistics_date").toString());
		}
		Date dateNow = new Date();
		while(startDate.before(dateNow)){
			String thisDateStr = sdf.format(startDate);
			if(!statisticedDayList.contains(thisDateStr) && !sdf.format(dateNow).equals(thisDateStr)){
				needStatistic.add(thisDateStr);
			}
			startDate = DateUtil.addDay(startDate, 1);
		}
		return needStatistic;
	}
}
