package ferenckovacsx.cognex.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ferenckovacsx.cognex.api.APIClient;
import ferenckovacsx.cognex.api.APIInterface;
import ferenckovacsx.cognex.R;
import ferenckovacsx.cognex.adapters.FirmwareAdapter;
import ferenckovacsx.cognex.models.Firmware;
import retrofit2.Call;
import retrofit2.Callback;


public class FirmwareListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    ListView firmwareListView;
    String deviceID, deviceName;
    TextView toolbarTitle, noDataTextView;
    ImageView backButton;
    SwipeRefreshLayout swipeRefreshLayout;

    ArrayList<Firmware> listOfFirmware = new ArrayList<>();

    APIInterface apiInterface;

    ProgressDialog progressDialog;

    final String TAG = "Firmware List Fragment";

    public FirmwareListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //retrofit
        APIClient apiClient = new APIClient(getActivity());
        apiInterface = apiClient.getClient().create(APIInterface.class);

        Bundle args = getArguments();
        deviceID = args.getString("id", "default");
        deviceName = args.getString("name", "default");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_firmware_list, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Retrieving data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firmwareListView = fragmentView.findViewById(R.id.listview_firmware);
        toolbarTitle = fragmentView.findViewById(R.id.toolbarDeviceTitle);
        backButton = fragmentView.findViewById(R.id.firmwareListBackButton);
        noDataTextView = fragmentView.findViewById(R.id.noDataTextView2);
        swipeRefreshLayout = fragmentView.findViewById(R.id.swiperefresh2);

        toolbarTitle.setText(deviceName);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                        getListOfFirmware();
                    }
                }
        );
        getListOfFirmware();

        Log.d(TAG, "DEVICE ID: " + deviceID);

        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    void getListOfFirmware() {

        Call<ArrayList<Firmware>> call = apiInterface.list_firmware(deviceID);



        call.enqueue(new Callback<ArrayList<Firmware>>() {

            @Override
            public void onResponse(@NonNull Call<ArrayList<Firmware>> call, @NonNull retrofit2.Response<ArrayList<Firmware>> response) {

                Log.d(TAG, "response: " + response.message());
                Log.d(TAG, "response: " + response);
                Log.d(TAG, "response: " + response.body());

                int responseCode = response.code();
                switch (responseCode) {
                    case 200:
                        Log.i(TAG, "Fetching data was successful. Number of firmware: " + response.body().size());

                        listOfFirmware = response.body();

                        progressDialog.cancel();
                        swipeRefreshLayout.setRefreshing(false);

                        FirmwareAdapter adapter = new FirmwareAdapter(getActivity(), listOfFirmware);
                        firmwareListView.setAdapter(adapter);
                        firmwareListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                                    long arg3)
                            {
                                Firmware selectedDevice = (Firmware) adapter.getItemAtPosition(position);
                                String description = selectedDevice.getDescription();
                                String title = selectedDevice.getTitle();
                                String created = selectedDevice.getCreated();
                                String version = selectedDevice.getVersion();
                                String filetype = selectedDevice.getFiletype();
                                String file = selectedDevice.getFile();
                                String channel = selectedDevice.getChannel();
                                int id = selectedDevice.getId();

                                Log.d(TAG, "onItemClick: " + deviceID);

                                FirmwareDescriptionFragment firmwareDescriptionFragment = new FirmwareDescriptionFragment();
                                Bundle args = new Bundle();
                                args.putString("description", description);
                                args.putString("title", title);
                                args.putString("created", created);
                                args.putString("version", version);
                                args.putString("filetype", filetype);
                                args.putString("file", file);
                                args.putString("channel", channel);
                                args.putInt("id", id);
                                firmwareDescriptionFragment.setArguments(args);

                                FragmentManager manager = getActivity().getFragmentManager();
                                FragmentTransaction transaction = manager.beginTransaction();
                                transaction.replace(R.id.fragment_container, firmwareDescriptionFragment, "");
                                transaction.addToBackStack(null);
                                transaction.commit();

                            }
                        });

                        if (listOfFirmware.size() > 0) {
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
            public void onFailure(@NonNull Call<ArrayList<Firmware>> call, @NonNull Throwable t) {
                Log.e(TAG, "Get files error" + t.toString());
                progressDialog.cancel();
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Failed to retrieve data. Please check your connection", Toast.LENGTH_SHORT).show();            }
        });
    }
}
