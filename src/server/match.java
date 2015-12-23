package server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.ActionSupport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class match extends ActionSupport implements 
                         ServletRequestAware,ServletResponseAware {
    /**
     * 
     */
	
	private String sourceCode;
	private String id;
	
	public String getSourceCode(){
		return sourceCode;
	}
	
	public void setSourceCode(String sourceCode){
		this.sourceCode=sourceCode;
	}
	
	public String getId(){
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
    private static final long serialVersionUID = 1L;
     
    HttpServletRequest request;
    HttpServletResponse response;

    public void setServletRequest(HttpServletRequest request) {
     this.request=request;
    }

    public void setServletResponse(HttpServletResponse response) {
        this.response=response;
    }

    public long HammingDistance_Analysis(long sourceBinary, long standardBinary, int judgmentBorder){
		boolean result = true;
		int notCloseRate = 0;		
		//change the problem to count the number of '1' in the sourceBinary.
		sourceBinary = sourceBinary^standardBinary;
		//the numbers of '1' in the sourceBinary is the "not close rate".
		//higher "not close rate" means less possible to be the same person.
		while(sourceBinary > 0){
			notCloseRate++;
			sourceBinary = sourceBinary & (sourceBinary - 1);
		}
		
		//notCloseRate above the judgmentBorder will be considered as different person
		if(notCloseRate > judgmentBorder){
			result = false;
		}
		
		//for debug
		//System.out.print(notCloseRate);
		
		return notCloseRate;
	}
    
    public void  analyse(){  
        String ret = ERROR;
        Connection conn = null;
    	try {
        	 String URL = "jdbc:mysql://localhost/iris_db";
        	 Class.forName("com.mysql.jdbc.Driver");
        	 conn = DriverManager.getConnection(URL, "root", "iceanderson");
        	 String sql = "SELECT iris_code FROM iris_list WHERE "+"id = ?";
        	 PreparedStatement ps = conn.prepareStatement(sql);
        	 
             this.response.setContentType("text/html;charset=utf-8");
             this.response.setCharacterEncoding("UTF-8");
             //String raw = sourceCode;//this.request.getParameter("sourceCode");
             //String rawlist[]=raw.split("!");
             long id = Long.parseLong(this.request.getParameter("id"));
             long sourceBinary = Long.parseLong(this.request.getParameter("sourceCode"));
             long standardBinary = 0;
             
             ps.setLong(1, id);
             
             ResultSet rs = ps.executeQuery();
             while(rs.next()){
            	 standardBinary = Long.parseLong(rs.getString("iris_code"));
             }
             
             long number = HammingDistance_Analysis(sourceBinary, standardBinary, 10);
             this.response.getWriter().write("HammingDistance="+Long.toString(number));
                 
   //          this.response.getWriter().write(ServletActionContext.getRequest().getMethod());
        } catch (Exception e) { 
            e.printStackTrace();
        }
        // return null;
    }
}
