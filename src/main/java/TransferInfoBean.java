
public class TransferInfoBean {
    private String week;
    private String day;
    private String amount;
    private String balance;
    private String summary;
    private String remark;
    private String time;
    private String date;
    private String account;
    private String ID;
    private boolean isNotBlank(String s) {
        return s != null && !s.trim().equals("");
    }
    private boolean isBlank(String s) {
        return s == null || s.trim().equals("");
    }
    public String getID() {
        String ac = isBlank(account) ? "" : account.equals("null") ? "" : account;
        if (isBlank(ac)) {
            return "";
        }
        String da = isBlank(time) ? "" : time.equals("null") ? "" : time;
        if (isBlank(da)) {
            return "";
        }
        return ac + da;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "TransferInfoBean{" +
                "week='" + week + '\'' +
                ", day='" + day + '\'' +
                ", amount='" + amount + '\'' +
                ", balance='" + balance + '\'' +
                ", summary='" + summary + '\'' +
                ", remark='" + remark + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                ", account='" + account + '\'' +
                ", ID='" + getID() + '\'' +
                '}';
    }
}
