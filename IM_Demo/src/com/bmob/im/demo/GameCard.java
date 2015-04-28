package com.bmob.im.demo;

public class GameCard   
{  
  
   private String gameName;
   private String gameRuleDetails;  
   private String gameWinMethod;
   private int mDrawable;  
   
   
     
   public GameCard(String gameName, String gameRuleDetails, String gameWinMethod,
		int mDrawable) {
	   super();
	   this.gameName = gameName;
	   this.gameRuleDetails = gameRuleDetails;
	   this.gameWinMethod = gameWinMethod;
	   this.mDrawable = mDrawable;
   }

   
   
   public String getGameRuleDetails() {
	   return gameRuleDetails;
   }



   public void setGameRuleDetails(String gameRuleDetails) {
	   this.gameRuleDetails = gameRuleDetails;
   }



   public String getGameWinMethod() {
	   return gameWinMethod;
   }



   public void setGameWinMethod(String gameWinMethod) {
	   this.gameWinMethod = gameWinMethod;
   }



   public String getGameName() {
	   return gameName;
   }

   public void setGameName(String gameName) {
		this.gameName = gameName;
	}
     
     
   public int getDrawable()   
   {  
    return mDrawable;  
   }  
     
   public void setDrawable(int mDrawable)   
   {  
    this.mDrawable = mDrawable;  
   }  
  
}  


