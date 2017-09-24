package com.ulfric.plugin.commands;

public interface Registry {

	void register(Command command);

	void unregister(Command command);

	Command getCommand(String name);

	void dispatch(Context context);

}