package com.magnamedia.report.wealth.table;

import com.magnamedia.report.ftm.table.*;
import com.magnamedia.core.helper.SelectFilter;
import com.magnamedia.core.helper.SelectQuery;
import com.magnamedia.extra.DateUtil;
import com.magnamedia.report.BaseReport;
import com.magnamedia.report.Cell;
import com.magnamedia.report.Color;
import com.magnamedia.report.Table;
import com.magnamedia.report.TableTitle;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ammar Esrawi <ammar.magna@gmail.com>
 * Created at Jan 20, 2018
 */
public abstract class BaseWealthTable extends Table {

    protected final Date from;
    protected final Date to;

    private static final Logger logger = Logger.getLogger(BaseReport.class
            .getName());

    public BaseWealthTable(Date from, Date to, Void v) {
        this.from = from;
        this.to = to;
    }

    public BaseWealthTable(Date from, Date to) {
        this.from = from;
        this.to = to;

        withTitle(new TableTitle(getName() )
                .withBackColor(getTitleBackColor())
                .withFontColor(getTitleColor())
        );

        try {
            build();
        } catch (Exception ex) {
            logger.log(Level.SEVERE,
                    ex.getMessage(),
                    ex);
            addRow(new Cell("Error Happened").withBackColor(Color.Red));

            
        }

    }

    protected SelectFilter getTimeFrameFilter( String field) {
        return getTimeFrameFilter(
                field,
                from,
                to);
    }

    protected SelectFilter getTimeFrameFilter(
            String field,
            Date from,
            Date to) {
        return new SelectFilter(field,
                ">=",
                from).and(field,
                "<=",
                to);
    }

    protected SelectFilter getTimeFrameFilter(String dateField,
            String timeField,
            Date from,
            Date to) {
        SelectFilter sf;

        sf = new SelectFilter(
                dateField, ">", new java.sql.Date(from.getTime())
        ).or(
                new SelectFilter(
                        dateField, "=", new java.sql.Date(from.getTime())
                ).and(new SelectFilter(
                        timeField, ">=", new java.sql.Time(from.getTime())
                ))
        ).and(new SelectFilter(
                dateField, "<", new java.sql.Date(to.getTime())
        ).or(
                new SelectFilter(
                        dateField, "=", new java.sql.Date(to.getTime())
                ).and(new SelectFilter(
                        timeField, "<=", new java.sql.Time(to.getTime())
                ))
        ));

        return sf;

    }

    public abstract String getName();

    public abstract void build();

    public Color getTitleBackColor() {
        return Color.Light_Blue;
    }
    public Color getTitleColor() {
        return Color.Black;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

}
