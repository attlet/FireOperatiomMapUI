package com.example.fireoperationmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> implements Filterable {
    private final List<User> userList = new ArrayList<>();
    private final List<User> userListFull = new ArrayList<>();
    private CustomAdapter.OnPersonItemClickLister listener;
    private String searchState = "st_name";

    public interface OnPersonItemClickLister {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(CustomAdapter.OnPersonItemClickLister listener) {
        this.listener = listener;
    }

    private final Filter customFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();
            if(constraint == null|| constraint.length() == 0){
                filteredList.addAll(userListFull);
            }
            else{
                String pattern = constraint.toString().toLowerCase().trim();
                for(User item : userListFull){
                    if (searchState.equals("st_name") && item.getSt_name().toLowerCase().contains(pattern)) {
                        filteredList.add(item);
                    }
                    else if (searchState.equals("address") && item.getAddress().toLowerCase().contains(pattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            userList.clear();
            userList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return customFilter;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView id, st_name, address, structure, floor, st_type, facility, person, phone1, phone2;
        CustomViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.id);
            address = itemView.findViewById(R.id.address);
            st_name = itemView.findViewById(R.id.st_name);
            structure = itemView.findViewById(R.id.structure);
            floor = itemView.findViewById(R.id.floor);
            st_type = itemView.findViewById(R.id.st_type);
            facility = itemView.findViewById(R.id.facility);
            person = itemView.findViewById(R.id.person);
            phone1 = itemView.findViewById(R.id.phone1);
            phone2 = itemView.findViewById(R.id.phone2);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    listener.onItemClick(view, position);
                }
            });
        }
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        User currentItem = userList.get(position);
        holder.id.setText(currentItem.getTagId());
        holder.address.setText(currentItem.getTagAddress());
        holder.floor.setText(currentItem.getTagFloor());
        holder.st_name.setText(currentItem.getTagSt_name());
        holder.structure.setText(currentItem.getTagStructure());
        holder.st_type.setText(currentItem.getTagSt_type());
        holder.facility.setText(currentItem.getTagFaclility());
        holder.person.setText(currentItem.getTagPerson());
        holder.phone1.setText(currentItem.getTagPhone1());
        holder.phone2.setText(currentItem.getTagPhone2());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public User getItem(int position) {
        return userList.get(position);
    }

    public void addUser(User user) {
        this.userListFull.add(user);
    }

    public void setSearchState(String searchState) {
        this.searchState = searchState;
    }
}