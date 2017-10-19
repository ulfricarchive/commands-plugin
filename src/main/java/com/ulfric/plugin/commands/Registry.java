package com.ulfric.plugin.commands;

public interface Registry {

	void register(Invoker command);

	void unregister(Invoker command);

	Invoker getCommand(String name);

	void dispatch(Context context);

}