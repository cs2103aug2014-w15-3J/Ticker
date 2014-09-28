package tickerPackage;



public class Task {
	public Task(){
		
	}
}

class Ticker{
	
	private Parser par;
    private static Ticker tk;
    private Logic log;
    
    public Logic getLogic(){
    	return log;
    }
    
    public Ticker(){
        par =  new Parser();
        tk = this;
        log = new Logic();
    }
    
    public static Ticker getTicker(){
    	return tk;
    }
    
    public static void main(String[] args){
        Ticker tk = new Ticker();
        while (true)
        	tk.par.getCommand();
    }
    
} 

class Logic{
	
	public Logic(){
		
	}

	
    
	public boolean delete(int i) {
		// TODO Auto-generated method stub
		System.out.println("delete");
		return false;
	}

	public boolean search(String str) {
		// TODO Auto-generated method stub
		System.out.println("search");
		return false;
	}

	public boolean list() {
		// TODO Auto-generated method stub
		System.out.println("list");
		return false;
	}

	public void edit(int index, boolean isAppending, String description) {
		// TODO Auto-generated method stub
		System.out.println("edit");
	}



	public void add(String description, Boolean isRepeating, Date sd, Date ed,
			Time st, Time et) {
		// TODO Auto-generated method stub
		System.out.println("add");
	}
}