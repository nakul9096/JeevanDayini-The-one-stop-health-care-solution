package com.example.unityapp;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;
public class UserListAdapter extends ArrayAdapter<Data2> {
    private List<Data2> userList;
    public UserListAdapter(@NonNull Context context, int resource, @NonNull List<Data2> userList) {
        super(context, resource, userList);
        this.userList = userList;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout, parent, false);
        }
        Data2 currentUser = userList.get(position);
        ImageView profilePictureImageView = listItemView.findViewById(R.id.profilePicture);
        TextView usernameTextView = listItemView.findViewById(R.id.username);
        profilePictureImageView.setImageResource(currentUser.getProfilePicture());
        usernameTextView.setText(currentUser.getUsername());
        return listItemView;
    }
}
