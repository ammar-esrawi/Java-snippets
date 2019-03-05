package com.magnamedia.report;

import com.magnamedia.extra.DateUtil;
import com.magnamedia.report.wealth.table.DetailedTotalExpenses;
import com.magnamedia.report.wealth.table.DetailedTotalRevenues;
import com.magnamedia.report.wealth.table.TotalExpenses;
import com.magnamedia.report.wealth.table.TotalRevenues;

import com.magnamedia.report.wealth.table.TotalWealth;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Ammar Esrawi <ammar.magna@gmail.com>
 * Created at Jan 20, 2018
 */
public class Wealth extends BaseReport {

    private final Date from;
    private final Date to;

    public Wealth() {
        from = getFrom();
        to = new Date();
    }

    @Override
    protected ReportTitle getTitle() {
        return new ReportTitle("W Report " + DateUtil
                .formatForTitle(to));
    }

    @Override
    public void build() {
        addSection(new TotalWealth(from, to));
        addNewLines(3);
        addSection(new TotalExpenses(from, to));
        addNewLines(3);
        addSection(new TotalRevenues(from, to));
        addNewLines(3);
        addSection(new DetailedTotalExpenses(from, to));        
        addNewLines(3);
        addSection(new DetailedTotalRevenues(from, to));
        addNewLines(3);
    }

    private Date getFrom() {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY,
                0);
        cal.set(Calendar.MINUTE,
                0);
        cal.set(Calendar.SECOND,
                0);
        cal.set(Calendar.MILLISECOND,
                0);
        return cal.getTime();
    }
}
