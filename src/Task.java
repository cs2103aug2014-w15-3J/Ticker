import java.util.Vector;


public class Task {
	public Task(){
		
	}
}

class Ticker{
	
	private Parser par;
    private static Ticker tk;
    public Ticker(){
        par =  new Parser();
        tk = this;
    }
    
    public static void main(String[] args){
        Ticker tk = new Ticker();
        while (true)
        	tk.par.getCommand();
    }
    
    public static Ticker getTicker(){
    	return tk;
    }
    
	public Vector<Task> delete(Task remove) {
		// TODO Auto-generated method stub
		return null;
	}

	public Vector<Task> search(String str) {
		// TODO Auto-generated method stub
		return null;
	}

	public Vector<Task> list() {
		// TODO Auto-generated method stub
		return null;
	}

} 

class Logic{
	
	public Logic(){
		
	}
	
	
}