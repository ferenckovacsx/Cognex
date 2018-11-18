package ferenckovacsx.cognex.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ferenckovacsx.cognex.api.APIClient;
import ferenckovacsx.cognex.api.APIInterface;
import ferenckovacsx.cognex.R;
import ferenckovacsx.cognex.adapters.DeviceAdapter;
import ferenckovacsx.cognex.models.Device;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity
        extends AppCompatActivity
        implements FirmwareListFragment.OnFragmentInteractionListener, FirmwareDescriptionFragment.OnFragmentInteractionListener {

    ArrayList<Device> listOfDevices = new ArrayList<>();

    APIInterface apiInterface;

    ListView listview;
    TextView noDataTextView, listviewHeader;
    SwipeRefreshLayout swipeRefreshLayout;

    final String TAG = "Main Activity";

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = findViewById(R.id.listview_devices);
        noDataTextView = findViewById(R.id.noDataTextView);
        listviewHeader = findViewById(R.id.listviewHeader);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Retrieving data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //retrofit
        APIClient apiClient = new APIClient(this);
        apiInterface = apiClient.getClient().create(APIInterface.class);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                        getListOfDevices();
                    }
                }
        );

        getListOfDevices();

    }

    void getListOfDevices() {

        Call<ArrayList<Device>> call = apiInterface.list_devices();

        call.enqueue(new Callback<ArrayList<Device>>() {

            @Override
            public void onResponse(@NonNull Call<ArrayList<Device>> call, @NonNull retrofit2.Response<ArrayList<Device>> response) {

                int responseCode = response.code();

                Log.d(TAG, "response code: " + responseCode);

                switch (responseCode) {
                    case 200:
                        Log.i(TAG, "Fetching data was successful. Number of devices: " + response.body().size());
                        progressDialog.cancel();
                        swipeRefreshLayout.setRefreshing(false);

                        listOfDevices = response.body();

                        DeviceAdapter deviceAdapter = new DeviceAdapter(MainActivity.this, listOfDevices);
                        listview.setAdapter(deviceAdapter);

                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                                    long arg3) {

                                Device selectedDevice = (Device) adapter.getItemAtPosition(position);
                                String deviceID = selectedDevice.getId();
                                String deviceName = selectedDevice.getName();

                                Log.d(TAG, "onItemClick: " + deviceID);

                                FirmwareListFragment firmwareListFragment = new FirmwareListFragment();
                                Bundle args = new Bundle();
                                args.putString("id", deviceID);
                                args.putString("name", deviceName);
                                firmwareListFragment.setArguments(args);

                                FragmentManager manager = getFragmentManager();
                                FragmentTransaction transaction = manager.beginTransaction();
                                transaction.replace(R.id.fragment_container, firmwareListFragment, "");
                                transaction.addToBackStack(null);
                                transaction.commit();

                            }
                        });

                        if (listOfDevices.size() > 0) {
                            noDataTextView.setVisibility(View.GONE);
                        } else {
                            noDataTextView.setVisibility(View.VISIBLE);
                        }

                        break;

                    default:
                        Log.i(TAG, "Unkwonwn error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Device>> call, @NonNull Throwable t) {
                Log.e(TAG, "Get files error: " + t.toString());
                progressDialog.cancel();
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "Failed to retrieve data. Please check your connection", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
