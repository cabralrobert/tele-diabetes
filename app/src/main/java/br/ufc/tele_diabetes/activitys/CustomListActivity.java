package br.ufc.tele_diabetes.activitys;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.ufc.tele_diabetes.R;
import br.ufc.tele_diabetes.utils.ItemDashboard;

/**
 * Created by robertcabral on 8/24/17.
 */

public class CustomListActivity extends BaseAdapter {
    private List<ItemDashboard> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomListActivity(Context context, List<ItemDashboard> listData) {
        this.listData = listData;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int i) {
        return listData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if(view == null){
            view = layoutInflater.inflate(R.layout.list_icon_layout,null);
            holder = new ViewHolder();
            holder.flag = (ImageView) view.findViewById(R.id.imageList);
            holder.name = (TextView) view.findViewById(R.id.nameList);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        ItemDashboard itemDashboard = this.listData.get(position);
        holder.name.setText(itemDashboard.getName());

        int idImage = this.getMipmapRasIdByName(itemDashboard.getFlag());

        holder.flag.setImageResource(idImage);

        return view;
    }

    public int getMipmapRasIdByName(String name){
        String pkgName = context.getPackageName();
        int resID = context.getResources().getIdentifier(name, "mipmap", pkgName);
        System.out.println("Res name: " + name + "\nRes ID: " + resID);
        return resID;
    }

    static class ViewHolder{
        ImageView flag;
        TextView name;
    }
}
