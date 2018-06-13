package ttdn.com.blockcaller.services;

import java.util.ArrayList;

import ttdn.com.blockcaller.interfaces.BlackListView;
import ttdn.com.blockcaller.models.MobileData;

/**
 * Created by Admin on 6/9/2018.
 */

public class BlackListPresenter {
    private BlackListView blacklistView;
    private BlackListService blackListService;

    public BlackListPresenter(BlackListView blacklistView, BlackListService blackListService) {
        this.blacklistView = blacklistView;
        this.blackListService = blackListService;
    }


    public ArrayList<MobileData> onSaveClick() {
        String smsName = blacklistView.getSmsName();
        String smsNumber = blacklistView.getSmsNumber();
        return blackListService.fetchBlackList();
    }
}
