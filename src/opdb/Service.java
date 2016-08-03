package opdb;

/**
 *
 * @author Joao
 */
public class Service {
    
    private double qnt;
    private String desc;
    private double price;
    
    public Service(double qnt,String desc, double price){
        this.qnt = qnt;
        this.desc = desc;
        this.price = price;
    }
    
    public double getQnt(){
        return this.qnt;
    }
    
    public String getDesc(){
        return this.desc;
    }
    
    public double getPrice(){
        return this.price;
    }
    
    public void setQnt(double qnt){
        this.qnt = qnt;
    }
    
    public void setDesc(String desc){
        this.desc = desc;
    }
    
    public void setPrice(double price){
        this.price = price;
    }
}
