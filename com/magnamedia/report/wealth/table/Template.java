package com.magnamedia.report.wealth.table;



import java.util.Date;


/**
 *
 * @author Ammar Esrawi <ammar.magna@gmail.com>
 * Created at Dec 18, 2017
 */
public class Template extends BaseWealthTable {

	public Template(Date from, Date to) {
		super(from,
			  to);
	}

	@Override
	public String getName() {
		return "Template Table";
	}

	@Override
	public void build() {

		
	}
        
        

	
}
