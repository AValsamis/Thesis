package uoa.di.gr.thesis.fragments.zones;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import uoa.di.gr.thesis.R;
import uoa.di.gr.thesis.entities.Zone;

public class ZoneArray extends ArrayAdapter<Zone> {
        private final LayoutInflater mInflater;
        private int[] colors = new int[] { 0xff9dbcbc, 0xffe4eaef };

        public ZoneArray(Context context) {
            super(context, android.R.layout.simple_list_item_2);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        public void setData(List<Zone> data) {
            clear();
            if (data != null) {
                for (Zone appEntry : data) {
                    add(appEntry);
                }
            }
        }

        /**
         * Populate new items in the list.
         */
        @Override public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                view = mInflater.inflate(R.layout.custom_zone_list, parent, false);
            } else {
                view = convertView;
            }
            Zone item = getItem(position);
            ((TextView)view.findViewById(R.id.tv_label)).setText(item.getFriendlyName());
            ((TextView)view.findViewById(R.id.tv_id)).setText(""+item.getZoneId());
            int colorPos = position % colors.length;
            view.setBackgroundColor(colors[colorPos]);
            return view;
        }
    }

