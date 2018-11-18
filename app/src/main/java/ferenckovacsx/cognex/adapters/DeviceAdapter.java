package ferenckovacsx.cognex.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ferenckovacsx.cognex.models.Device;
import ferenckovacsx.cognex.R;


public class DeviceAdapter extends ArrayAdapter<Device> {

    private Context mContext;
    private List<Device> deviceList;

    public DeviceAdapter(@NonNull Context context, ArrayList<Device> list) {
        super(context, 0, list);
        mContext = context;
        deviceList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);

        Device device = deviceList.get(position);

        TextView name = listItem.findViewById(R.id.listItemTextView);
        name.setText(device.getName());

        return listItem;
    }
}