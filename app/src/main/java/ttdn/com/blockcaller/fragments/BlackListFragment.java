package ttdn.com.blockcaller.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import ttdn.com.blockcaller.R;
import ttdn.com.blockcaller.adapters.BlackListAdapter;
import ttdn.com.blockcaller.interfaces.BlackListView;
import ttdn.com.blockcaller.services.BlackListPresenter;
import ttdn.com.blockcaller.services.BlackListService;


public class BlackListFragment extends Fragment implements BlackListView {
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private BlackListPresenter blackListPresenter;
    private RelativeLayout relative_help;
    private BlackListAdapter mAdapter;

    public BlackListFragment() {
        // Required empty public constructor
    }

    private void initView(View rootView) {
        relative_help = (RelativeLayout) rootView.findViewById(R.id.relative_help);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyview_fragment_blacklist);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView;
        rootView = inflater.inflate(R.layout.fragment_black_list, container, false);
        initView(rootView);
        blackListPresenter = new BlackListPresenter(this, new BlackListService());
        mAdapter = new BlackListAdapter(blackListPresenter.onSaveClick(), getActivity());
        mAdapter.setOnDataChangeListener(new BlackListAdapter.OnDataChangeListener() {
            @Override
            public void onDataChanged(int size) {
                setMessage(size);
            }
        });
        setMessage(blackListPresenter.onSaveClick().size());
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    // hiện thị nội dụng trợ giúp trong fragment nếu Recyview rỗng
    public void setMessage(int size) {
       // Toast.makeText(getContext(),"Size black list = "+size,Toast.LENGTH_SHORT).show();
        if (size > 0) {

            relative_help.setVisibility(View.GONE);
        } else {
            relative_help.setVisibility(View.VISIBLE);
        }
    }

    public void updateListView() {
        //snippet will call the onCreateView Method of the Fragment.
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public void getSmsInfo() {

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
