package com.magnamedia.report.wealth.table;


import com.magnamedia.core.helper.SelectQuery;
import com.magnamedia.entity.Contract;
import com.magnamedia.entity.Expense;
import com.magnamedia.entity.Transaction;
import com.magnamedia.extra.ContractStatus;
import com.magnamedia.extra.NumberFormater;
import com.magnamedia.report.Cell;
import com.magnamedia.report.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Ammar Esrawi <ammar.magna@gmail.com>
 * Created at Dec 18, 2017
 */
public class TotalExpenses extends BaseWealthTable {

    public TotalExpenses(Date from, Date to) {
        super(from,
                to);
    }

    @Override
    public Color getTitleBackColor() {
        return Color.Orange;
    }
    @Override
    public String getName() {
        return "Expenses Summary";
    }

    @Override
    public void build() {

        //cols
        addRow(new Cell("#").withBackColor(Color.Light_Grey),
                new Cell("Name of Expense")
                        .withBackColor(Color.Light_Grey),
                new Cell("Related entities").withBackColor(Color.Light_Grey),
                new Cell("Amount").withBackColor(Color.Light_Grey)
        );

        //add data
        List<Transaction> transactions = transactions(getFrom(), getTo()).execute();
        List<ExpenseWrapper> expenses = new ArrayList<ExpenseWrapper>();
        for (Transaction t : transactions) {
            ExpenseWrapper ew = new ExpenseWrapper(t.getExpense());
            int index = expenses.indexOf(ew);
            if (index != -1) {
                expenses.get(index).addTransaction(t);
            } else {
                ew.addTransaction(t);
                expenses.add(ew);
            }
        }

        expenses.sort((o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }

            ExpenseWrapper e1 = (ExpenseWrapper) o1;
            ExpenseWrapper e2 = (ExpenseWrapper) o2;
            if (e1.getAmmount() > e2.getAmmount()) {
                return -1;
            }
            if (e1.getAmmount() < e2.getAmmount()) {
                return 1;
            }
            return 0;
        });

        int index = 1;
        int i = 0;
        double total = 0;
        for (ExpenseWrapper e : expenses) {
            index = e.generateIndecies(index);
            addRow(new Cell("" + (++i)),
                    new Cell(e.getExpense().getName().replace("&", "and")),
                    new Cell(e.getTransactionsChain()),
                    new Cell("" + NumberFormater.thousandSeperated( e.getAmmount()))
            );
            total += e.getAmmount();

        }
        addRow(new Cell("<strong>Total</strong>").withBackColor(Color.Light_Grey).withColumnSpan(3),
                new Cell("AED " +  NumberFormater.thousandSeperated(total)).withBackColor(Color.Light_Grey)
        );

    }

    private SelectQuery<Transaction> transactions(Date from, Date to) {
        SelectQuery<Transaction> query = new SelectQuery<>(Transaction.class);
        query.filterBy(getTimeFrameFilter(
                "date",
                from,
                to));

        query.filterBy("expense",
                "is not null",
                null);

        return query;
    }

    private class ExpenseWrapper {

        private Expense expense;
        private double ammount;
        private int start;
        private List<Transaction> transactions;

        public ExpenseWrapper(Expense expense) {

            this.expense = expense;
        }

        public void addTransaction(Transaction t) {
            if (transactions == null) {
                transactions = new ArrayList<Transaction>();
            }
            transactions.add(t);
            ammount += t.getAmount();
        }

        public String getTransactionsChain() {
            if (transactions.size() == 1) {
                return start + "";
            }
            return "" + start + "-->" + (start + transactions.size() - 1);
        }

        public int generateIndecies(int start) {
            this.start = start;
            return this.transactions.size() + start;

        }

        public int getIndex(Transaction t) {
            int i = transactions.indexOf(t);
            if (i != -1) {
                return i + start;

            } else {
                return -1;
            }
        }

        public double getAmmount() {
            return ammount;
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }

        public Expense getExpense() {
            return expense;
        }

        public void setExpense(Expense expense) {
            this.expense = expense;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ExpenseWrapper other = (ExpenseWrapper) obj;
            if (!Objects.equals(this.expense, other.expense)) {
                return false;
            }
            return true;
        }
    }


}
