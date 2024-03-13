package com.example.unityapp;
import android.os.Bundle;
import android.widget.ListView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import java.util.List;
public class DonarListView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donar_list_view);
        ListView listView = findViewById(R.id.listView);
        List<Data2> userList = new ArrayList<>();
        userList.add(new Data2(R.drawable.profile, "User 1"));
        userList.add(new Data2(R.drawable.profile, "User 2"));
        UserListAdapter adapter = new UserListAdapter(this, R.layout.list_item_layout, userList);
        listView.setAdapter(adapter);
    }
}