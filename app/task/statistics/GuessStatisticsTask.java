package task.statistics;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import common.task.Task;
import jws.Logger;

/**
 * 
 * 定时按天统计疯狂夺宝运营数据
 */
public class GuessStatisticsTask extends Task {

	@Override
	public void run() {
		List<String> needStatisticDay = DollStatisticsTask.getStatisticDate("guess_statistics");
		if(needStatisticDay.size() == 0){
			return;
		}
		for(String date : needStatisticDay){
			Logger.info("统计疯狂夺宝数据:"+date);
			
			try{
				List<Record> rec2 = getRec2(date);
				if(rec2.size() != 3){
					Logger.warn("疯狂夺宝场次有调整，统计服务无法执行");
					return;
				}
				Record rec1 = getRec1(date);
				StringBuffer sql = new StringBuffer();
				sql.append("INSERT INTO guess_statistics(statistics_date,play_num,order_num_1,join_num_1,")
				.append("order_num_2,join_num_2,order_num_3,join_num_3,deduct_beans,putin_beans,dc_beans,")
				.append("return_rate,deduct_return_rate,win_rate,create_time)");
				sql.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,now())");
				List<Object> params = new ArrayList<Object>();
				params.add(date);
				params.add(rec1.get("play_num") == null ? 0 : rec1.get("play_num"));
				for(Record r : rec2){
					params.add(r.get("order_num"));
					params.add(r.get("person_num"));
				}
				params.add(rec1.get("deduct_beans") == null ? 0 : rec1.get("deduct_beans"));
				params.add(rec1.get("putin_beans") == null ? 0 : rec1.get("putin_beans"));
				params.add(rec1.get("dc_beans") == null ? 0 : rec1.get("dc_beans"));
				params.add(rec1.get("return_rate") == null ? 0 : rec1.get("return_rate"));
				params.add(rec1.get("deduct_return_rate") == null ? 0 : rec1.get("deduct_return_rate"));
				params.add(rec1.get("win_rate") == null ? 0 : rec1.get("win_rate"));
				Db.update(sql.toString(), params.toArray());
			}catch (Exception e) {
				Logger.info("没有成功插入疯狂夺宝数据（"+e.getMessage()+"），可能是其他机器的定时器已做了统计");
			}
		}
		
	}
	
	/**
	 * 获取玩游戏人数、抽成、投入开心豆、兑成开心豆、返奖率、抽成后返奖率、中奖率
	 * @return
	 */
	private Record getRec1(String statisticDay){
		StringBuffer sql = new StringBuffer();
		sql.append("select sum(deduct_bean) deduct_beans,");
		sql.append("sum(happy_bean) putin_beans,sum(reward_bean) dc_beans,sum(reward_bean)/sum(happy_bean) return_rate,");
		sql.append("sum(reward_bean-deduct_bean)/sum(happy_bean) deduct_return_rate,sum(IF(reward_bean>0, 1, 0))/count(*) win_rate ");
		sql.append("from guess_record where is_robot=0 and date(create_time) = ? ");
		Record rec = Db.findFirst(sql.toString(), statisticDay);
		
		Record playNum = Db.findFirst("select count(DISTINCT uid) play_num from member_log where date(op_time)=? and game_id=1000001 ", statisticDay);
		rec.set("play_num", playNum.get("play_num"));
		return rec;
	}
	
	/**
	 * 各个场次的订单数、人数
	 */
	private List<Record> getRec2(String statisticDay){
		StringBuffer sql = new StringBuffer();
		sql.append("select count(grec.id) order_num,count(DISTINCT uid) person_num ");
		sql.append("from guess_award gaw left join guess_record grec on (grec.is_robot=0 and date(grec.create_time) = ? and gaw.id=grec.guess_award_id) ");
		sql.append("group by gaw.game_level ORDER BY gaw.game_level ");
		
		return Db.find(sql.toString(),statisticDay);
	}

}
