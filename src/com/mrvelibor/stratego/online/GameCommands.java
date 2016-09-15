package com.mrvelibor.stratego.online;

public interface GameCommands {
	
	public static final String COMMAND_CONFIRM_MOVE = ServerCommands.COMMAND_GAME + "y";
	
	public static final String COMMAND_DENY_MOVE = ServerCommands.COMMAND_GAME + "n";
	
	public static final String COMMAND_READY = ServerCommands.COMMAND_GAME + "r";
	
	public static final String COMMAND_PLACE = ServerCommands.COMMAND_GAME + "c";
	
	public static final String COMMAND_PLAY = ServerCommands.COMMAND_GAME + "p";
	
	public static final String COMMAND_WIN = ServerCommands.COMMAND_GAME + "w";
	
	public static final String COMMAND_LOSE = ServerCommands.COMMAND_GAME + "l";
	
	public static final String COMMAND_PING = ServerCommands.COMMAND_GAME + "m";
	
	public static final String COMMAND_TEAM = ServerCommands.COMMAND_GAME + "t";
	
}
