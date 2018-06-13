package ttdn.com.blockcaller.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ttdn.com.blockcaller.CommonDb;
import ttdn.com.blockcaller.R;
import ttdn.com.blockcaller.models.MobileData;

/**
 * Created by Admin on 6/9/2018.
 */

public class BlackListAdapter extends RecyclerView.Adapter<BlackListAdapter.ViewHolder> {
    private ArrayList<MobileData> mDataset;
    private Context context;
    OnDataChangeListener mOnDataChangeListener;

    public BlackListAdapter(ArrayList<MobileData> myDataset, Context context) {
        mDataset = myDataset;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_black_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        MobileData data = mDataset.get(position);
        if (data.getCallerName().matches("\\d+")) {
            holder.imgPersonPhoto.setImageResource(R.drawable.ic_message_black_24dp);

        } else {
            holder.imgPersonPhoto.setImageResource(R.drawable.ic_phone_black_24dp);

        }

        holder.mTxtNumber.setText(data.getMobileNumber().toString());
        holder.mTxtName.setText(data.getCallerName().toString());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.title_delete_item))
                        .setMessage(context.getString(R.string.msg_delete_item))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(context.getString(R.string.discard_dialog_button_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                boolean res = deleteItem(position);
                                Toast.makeText(context, "Result delete =" + res, Toast.LENGTH_SHORT).show();
                                doButtonOneClickActions();
                            }
                        })
                        .setNegativeButton(context.getString(R.string.discard_dialog_button_no), null).show();
            }
        });
    }

    public boolean deleteItem(int position) {
        CommonDb commonDbMethod = new CommonDb(context);
        boolean res = commonDbMethod.deleteBlackListNumber(mDataset.get(position).getMobileNumber(), CommonDb.TABLE_BLACK_LIST);
        mDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataset.size());
        return res;
    }

    private void doButtonOneClickActions() {
        if (mOnDataChangeListener != null) {
            mOnDataChangeListener.onDataChanged(mDataset.size());
            notifyDataSetChanged();
        }
    }

    public MobileData getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTxtNumber;
        public TextView mTxtName;
        public ImageButton btnDelete;
        ImageView imgPersonPhoto;

        public ViewHolder(View v) {
            super(v);
            mTxtName = (TextView) v.findViewById(R.id.person_name);
            mTxtNumber = (TextView) v.findViewById(R.id.person_number);
            btnDelete = (ImageButton) v.findViewById(R.id.btnDelete);
            imgPersonPhoto = (ImageView) v.findViewById(R.id.person_photo);
        }
    }

    public interface OnDataChangeListener {
        void onDataChanged(int size);
    }

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener) {
        mOnDataChangeListener = onDataChangeListener;
    }
}
