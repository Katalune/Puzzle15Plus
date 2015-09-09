package ua.pp.kata.puzzle15plus;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter to show picture with text in drawer list.
 */
public class DrawerListAdapter extends ArrayAdapter<String>{
    private final Activity context;
    private final String[] items;
    private final Integer[] imageId;

    public DrawerListAdapter(Activity context, String[] items, Integer[] imageId) {
        super(context, R.layout.item_drawer_list, items);
        this.context = context;
        this.items = items;
        this.imageId = imageId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View root = inflater.inflate(R.layout.item_drawer_list, null, true);
        TextView txt = (TextView) root.findViewById(R.id.txt);
        ImageView img = (ImageView) root.findViewById(R.id.img);

        txt.setText(items[position]);
        img.setImageResource(imageId[position]);

        return root;
    }
}
