package ferenckovacsx.cognex;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    ArrayList<Device> listOfDevices = new ArrayList<>();

    APIInterface apiInterface;

    ListView listview;
    
    final String TAG = "Main Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = findViewById(R.id.listview_devices);

        //retrofit
        APIClient apiClient = new APIClient(this);
        apiInterface = apiClient.getClient().create(APIInterface.class);

        getListOfDevices();

    }

    void getListOfDevices() {

        Call<ArrayList<Device>> call = apiInterface.list_devices();

        call.enqueue(new Callback<ArrayList<Device>>() {

            @Override
            public void onResponse(Call<ArrayList<Device>> call, retrofit2.Response<ArrayList<Device>> response) {

                int responseCode = response.code();
                switch (responseCode) {
                    case 200:
                        Log.i(TAG, "Fetching data was successful. Number of devices: " + response.body().size());

                        listOfDevices = response.body();

                        ArrayList<String> listOfDevicesString = new ArrayList<>();

                        for (int i = 0; i < listOfDevices.size(); i++) {
                            listOfDevicesString.add(listOfDevices.get(i).getName());
                        }

                        ArrayAdapter adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, listOfDevicesString);

                        listview.setAdapter(adapter);

//                        if (listOfDevices.size() > 0) {
//                            emptyListTextView.setVisibility(View.GONE);
//                            documentsHeaderTextView.setVisibility(View.VISIBLE);
//                        } else {
//                            emptyListTextView.setVisibility(View.VISIBLE);
//                            documentsHeaderTextView.setVisibility(View.GONE);
//                        }

//                        //recyclerview with custom adapter
//                        recyclerView.setHasFixedSize(true);
//                        layoutManager = new LinearLayoutManager(MainActivity.this);
//                        recyclerView.setLayoutManager(layoutManager);
//
//                        //add divider between list items
//                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), 1);
//                        recyclerView.addItemDecoration(dividerItemDecoration);
//
//                        listAdapter = new DocumentsListAdapter(listOfDevices);
//                        recyclerView.setAdapter(listAdapter);
//
//                        listAdapter.setOnItemSelectedListener(new DocumentsListAdapter.OnItemSelectedListener() {
//                            @Override
//                            public void onItemSelected(int position, String uuid) {
//                                deleteDocumentButton.setVisibility(View.VISIBLE);
//                                selectedItemPosition = position;
//                                selectedItemUUID = uuid;
//                                Log.i(TAG, "Item is selected: " + uuid);
//                            }
//                        });

                        break;

                    default:
                        Log.i(TAG, "Unkwonwn error");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Device>> call, Throwable t) {
                Log.e(TAG, "Get files error" + t.toString());
            }
        });
    }
}
