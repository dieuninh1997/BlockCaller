package ttdn.com.blockcaller.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ttdn.com.blockcaller.R;
import ttdn.com.blockcaller.adapters.LogAdapter;
import ttdn.com.blockcaller.interfaces.BlockedListView;
import ttdn.com.blockcaller.services.BlockedListPresenter;
import ttdn.com.blockcaller.services.BlockedListService;

/**

 */
public class LogFragment extends Fragment implements BlockedListView{
    private RecyclerView.LayoutManager mLayoutManager;
    RecyclerView recyclerView;
    TextView textView;
    BlockedListPresenter blockedListPresenter;

    public LogFragment() {
        // Required empty public constructor
    }

    private void initView(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
        textView = (TextView) rootView.findViewById(R.id.textView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_log, container, false);
        initView(rootView);
        blockedListPresenter = new BlockedListPresenter(this, new BlockedListService());
        LogAdapter mAdapter = new LogAdapter(blockedListPresenter.onFetchClick(), getActivity());
        recyclerView.setAdapter(mAdapter);
        setEmptyMessage(blockedListPresenter.onFetchClick().size());

        mAdapter.setOnDataChangeListener(new LogAdapter.OnDataChangeListener() {
            @Override
            public void onDataChanged(int size) {
                setEmptyMessage(size);
            }
        });

        return rootView;
    }

    public void setEmptyMessage(int size) {
        if (size > 0) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }

    public void reloadWhenDataChanges(){
        //snippet will call the onCreateView Method of the Fragment.
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public String getSmsName() {
        return null;
    }

    @Override
    public String getSmsNumber() {
        return null;
    }
}
