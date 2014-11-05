package ticker.parser;
//@author A0115369B
public enum CMD{
	ERROR("error"),DEL("delete"),EDIT("edit"),EDITT("editt"),
	SEARCH("search"),ADD("add"),TICK("tick"),UNTICK("untick"),
	KIV("kiv"),UNKIV("unkiv"),CLEAR("clear"),EXIT("exit"),
	UNDO("undo"),REDO("redo"),HELP("help"),LIST("list");
	private final String text;
	private CMD(final String text){
		this.text=text;
	}
	@Override
	public String toString(){
		return text;
	}
}