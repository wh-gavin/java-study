package ex20.pyrmont.modelmbeantest1;

public class Car {
  
	  private String color = "red";
	  
	  public String getColor() {
	    System.out.println("[Car] getColor() 被调用，返回: " + color);
	    return color;
	  }
	  
	  public void setColor(String color) {
	    System.out.println("[Car] setColor() 被调用，新颜色: " + color);
	    this.color = color;
	  }
	  
	  public void drive() {
	    System.out.println("[Car] drive() 被调用: Baby you can drive my car.");
	  }
}
