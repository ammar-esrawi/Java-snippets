package com.magnamedia.report.wealth.table;

import com.magnamedia.core.Setup;
import com.magnamedia.core.helper.Aggregate;
import com.magnamedia.core.helper.AggregateQuery;
import com.magnamedia.core.helper.SelectQuery;
import com.magnamedia.entity.Bucket;
import com.magnamedia.entity.Transaction;
import com.magnamedia.extra.BucketsInformation;
import com.magnamedia.extra.NumberFormater;
import com.magnamedia.report.Cell;
import com.magnamedia.report.Color;
import com.magnamedia.report.Size;
import com.magnamedia.repository.BucketRepository;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author Ammar Esrawi <ammar.magna@gmail.com>
 * Created at Dec 18, 2017
 */
public class TotalWealth extends BaseWealthTable {

    public TotalWealth(Date from, Date to) {
        super(from,
                to);
    }

    @Override
    public String getName() {
        return "";
    }

    public Color getTitleBackColor() {
        return Color.White;
    }

    @Override
    public void build() {

        double totalWealth = 0;

        //bank buckets
        totalWealth += getBucketsChunck(Arrays.asList(
                "BC 25",
                "BC 10",
                "BC 27",
                "BC 01",
                "BC 26",
                "BC 02",
                "BC 11",
                "BC 31"
        ), "Bank Buckets", Color.Yellow);

        //Cards buckets
        totalWealth += getBucketsChunck(Arrays.asList(
               // "BC 19",
                //"BC 05",
                "BC 23",
                "BC 06",
                "BC 04"
        ), "Cards", Color.Orange);

        //Visa Processing buckets
        totalWealth += getBucketsChunck(Arrays.asList(
                "BC 22",
                "BC 03",
                "BC 08"
        ), "Visa Processing", Color.Green);

        //Petty Cash Holders buckets
        totalWealth += getBucketsChunck(Arrays.asList(
                "BC 12",
                //"BC 32",
               // "BC 16",
                "BC 13",
                "BC 14",
                //"BC 34",
                "BC 33",
                "BC 35",
                "BC 41",
                "BC 15"
        ), "Petty Cash Holders", Color.Blue);

        //TOTAL WEALTH
        addRow(
                new Cell("<strong>Total Wealth</strong>").withBackColor(Color.Amber).withFontSize(Size.XLarge),
                new Cell("<strong>AED " + NumberFormater.thousandSeperated(totalWealth) + "</strong>").withBackColor(Color.Amber).withFontSize(Size.XLarge)
        );

        totalWealth = 0;

        //bank buckets
        totalWealth += addBucketsChunck(Arrays.asList(
                "BC 25",
                "BC 10",
                "BC 27",
                "BC 01",
                "BC 26",
                "BC 02",
                "BC 11",
                "BC 31"
        ), "Bank Buckets", Color.Yellow);

        //Cards buckets
        totalWealth += addBucketsChunck(Arrays.asList(
                //"BC 19",
                //"BC 05",
                "BC 23",
                "BC 06",
                "BC 04"
        ), "Cards", Color.Orange);

        //Visa Processing buckets
        totalWealth += addBucketsChunck(Arrays.asList(
                "BC 22",
                "BC 03",
                "BC 08"
        ), "Visa Processing", Color.Green);

        //Petty Cash Holders buckets
        totalWealth += addBucketsChunck(Arrays.asList(
                "BC 12",
                //"BC 32",
                //"BC 16",
                "BC 13",
                "BC 14",
                //"BC 34",
                "BC 33",
                "BC 35",
                "BC 41",
                "BC 15"
        ), "Petty Cash Holders", Color.Blue);

        //total Unknown
        addRow(
                new Cell("Unknown Wire Transfers outstanding balance").withBackColor(Color.Grey),
                new Cell("AED " + NumberFormater.thousandSeperated(totalUnknown()) + "").withBackColor(Color.Grey)
        );
    }

    private double addBucketsChunck(List<String> buckets, String name, Color c) {
        List<Bucket> bBuckets = getBuckets(buckets);
        double total = getBucketsBalances(bBuckets);

        addRow(
                new Cell(name).withColumnSpan(2).withBackColor(c)
        );
        addRow(
                new Cell("Name Of Bucket").withBackColor(Color.Light_Grey),
                new Cell("Balance").withBackColor(Color.Light_Grey)
        );
        for (Bucket b : bBuckets) {
            addRow(
                    new Cell(b.getName().replace("&", "and")),
                    new Cell("" + NumberFormater.thousandSeperated(
                            getBucketsBalances(Arrays.asList(b))))
            );
        }
        addRow(
                new Cell("Total " + name + " Wealth").withBackColor(c),
                new Cell("AED " + NumberFormater.thousandSeperated(total)).withBackColor(c)
        );

        return total;
    }

    private double getBucketsChunck(List<String> buckets, String name, Color c) {
        List<Bucket> bBuckets = getBuckets(buckets);
        double total = getBucketsBalances(bBuckets);

        return total;
    }

    private List<Bucket> getBuckets(List<String> codes) {
        SelectQuery<Bucket> query = new SelectQuery<>(Bucket.class);
        query.filterBy("code",
                "in",
                codes);
        query.sortBy("name", true);
        return query.execute();
    }

    private double totalUnknown() {
        SelectQuery<Transaction> query = new SelectQuery<>(Transaction.class);
        query.filterBy("revenue.code",
                "like",
                "%uwr%");
        query.filterBy("revenue",
                "is not null",
                null);
        query.filterBy("toBucket",
                "is not null",
                null);
//        query.filterBy(getTimeFrameFilter("creationDate", getFrom(), getTo()));

        return new AggregateQuery(query, Aggregate.Sum, "amount").execute().longValue();
    }

    private double total(List<Bucket> buckets) {
        double total = 0;
        for (Bucket b : buckets) {
            total += b.getBalance();
        }
        return total;
    }

    private double getBucketsBalances(List<Bucket> buckets) {
        double result = 0;

        if (buckets.size() > 0) {
            List<BucketsInformation> balances = Setup.getRepository(BucketRepository.class)
                    .getBucketsBalanceInformation2(buckets.stream().map(x -> x.getId()).collect(Collectors.toList()));
            Map<Long, BucketsInformation> mapBalances = new HashMap<Long, BucketsInformation>();
            for (BucketsInformation info : balances) {
                mapBalances.put(info.getID(), info);
            }
            for (Bucket b : buckets) {
                BucketsInformation buckBalance = mapBalances.get(b.getId());
                result += buckBalance.getTOTALSUM() - buckBalance.getTOTALMINUS() + (b.getInitialBalance() == null ? 0 : b.getInitialBalance());
                //b.setBalance(finalBalance);
            }
        }

        return result;
    }

}
