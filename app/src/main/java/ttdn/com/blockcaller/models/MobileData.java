package ttdn.com.blockcaller.models;

/**
 * Created by Admin on 6/9/2018.
 */

public class MobileData {
    private String smsNo;
    private String smsString;
    private String smsAddress;
    private String smsId;

    public String getSmsThreadNo() {
        return smsNo;
    }

    public void setCallerName(String smsNo) {
        this.smsNo = smsNo;
    }
    public String getCallerName(){
        return smsNo;
    }

    public String getOtherString() {
        return smsString;
    }

    public void setOtherString(String smsString) {
        this.smsString = smsString;
    }

    public String getMobileNumber() {
        return smsAddress;
    }

    public void setMobileNumber(String smsAddress) {
        this.smsAddress = smsAddress;
    }

    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }
}
