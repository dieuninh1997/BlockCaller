package ttdn.com.blockcaller.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ttdn.com.blockcaller.R;
import ttdn.com.blockcaller.models.NumberData;

/**
 * Created by Admin on 6/9/2018.
 */
// lấy danh sách trong nhật ký cuộc gọi đưa vào RecyclerView
public class LogNumberAdapter extends BaseAdapter {
    private ArrayList<NumberData> optionDataArrayList;
    private Activity context;
    private LayoutInflater mInflater;
    private boolean subTitle = false;

    public LogNumberAdapter(Activity context, ArrayList<NumberData> optionDatas, boolean subtitle) {
        this.optionDataArrayList = optionDatas;
        this.context = context;
        this.subTitle = subtitle;
    }

    @Override
    public int getCount() {
        return this.optionDataArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.optionDataArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView title;
        TextView txtViewName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            holder = new ViewHolder();
            if (subTitle) {
                convertView = mInflater.inflate(R.layout.item_subtitle, null);
                holder.txtViewName = convertView.findViewById(R.id.subtitle);
                holder.title = convertView.findViewById(R.id.title);
            } else {
                convertView = mInflater.inflate(R.layout.item_number_list, null);
                holder.txtViewName = (TextView) convertView.findViewById(R.id.name);

            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (subTitle) {
            holder.title.setText(optionDataArrayList.get(position).getName());
        }
        holder.txtViewName.setText(optionDataArrayList.get(position).getSenderNumber());

        return convertView;
    }
}
