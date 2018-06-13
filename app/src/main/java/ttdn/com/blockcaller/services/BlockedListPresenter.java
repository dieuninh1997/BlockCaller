package ttdn.com.blockcaller.services;

import java.util.ArrayList;

import ttdn.com.blockcaller.interfaces.BlockedListView;
import ttdn.com.blockcaller.models.MobileData;

/**
 * Created by Admin on 6/9/2018.
 */

public class BlockedListPresenter {
    private BlockedListView blacklistView;
    private BlockedListService blackListService;

    public BlockedListPresenter(BlockedListView blacklistView, BlockedListService blackListService) {
        this.blacklistView = blacklistView;
        this.blackListService = blackListService;
    }

    public ArrayList<MobileData> onFetchClick() {
        String smsName = blacklistView.getSmsName();
        String smsNumber = blacklistView.getSmsNumber();

        return blackListService.fetchBlockedList();
    }
}
