package constants;

import java.util.Arrays;
import java.util.List;

public enum SelfGame {
	// 自己的游戏从100w开始，不太可能对外接入100w款游戏
	GAME_DOLL(1000000, "夹娃娃"), 
	
	GAME_GUESS(1000001, "疯狂夺宝"),
	
	GAME_ENVELOPPE(1000002, "欢乐抢红包");

	private Integer gameId; // 场次
	private String gameName; // 描述

	private SelfGame(int gameId, String gameName) {
		this.gameId = gameId;
		this.gameName = gameName;
	}

	public Integer getGameId() {
		return gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

}
