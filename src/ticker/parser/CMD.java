package ticker.parser;
//@author A0115369B

/*
 * This enum stores all the commands(key words) that are supported by parser
 */

public enum CMD{
	ERROR("error"), DEL("delete"), EDIT("edit"), EDITT("editt"),
	SEARCH("search"), ADD("add"), TICK("tick"), UNTICK("untick"),
	KIV("kiv"), UNKIV("unkiv"), CLEAR("clear"), EXIT("exit"),
	UNDO("undo"), REDO("redo"), HELP("help"), LIST("list"), 
	SEARCHFREE("searchfree"), TAKE("take");
	
	private final String text;
	private CMD(final String text){
		this.text = text;
	}
	@Override
	public String toString(){
		return text;
	}
}