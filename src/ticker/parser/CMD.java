package ticker.parser;

public enum CMD{
	ERROR("error"),DEL("delete"),EDIT("edit"),EDITT("editt"),
	SEARCH("search"),ADD("add"),TICK("tick"),UNTICK("untick"),
	CMI("cmi"),UNCMI("uncmi"),CLEAR("clear"),EXIT("exit"),
	UNDO("undo"),REDO("redo");
	private final String text;
	private CMD(final String text){
		this.text=text;
	}
	@Override
	public String toString(){
		return text;
	}
}