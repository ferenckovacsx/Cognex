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

import ferenckovacsx.cognex.R;
import ferenckovacsx.cognex.models.Firmware;


public class FirmwareAdapter extends ArrayAdapter<Firmware> {

    private Context mContext;
    private List<Firmware> firmwareList;

    public FirmwareAdapter(@NonNull Context context, ArrayList<Firmware> list) {
        super(context, 0, list);
        mContext = context;
        firmwareList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);

        Firmware Firmware = firmwareList.get(position);

        TextView name = listItem.findViewById(R.id.listItemTextView);
        name.setText(Firmware.getTitle());

        return listItem;
    }
}