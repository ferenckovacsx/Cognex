package ferenckovacsx.cognex;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;


public class FirmwareListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    ListView firmwareListView;
    String deviceID;
    TextView toolbarTitle;

    ArrayList<Firmware> listOfFirmware = new ArrayList<>();

    APIInterface apiInterface;

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
        deviceID = args.getString("title", "default");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_firmware_list, container, false);

        firmwareListView = fragmentView.findViewById(R.id.listview_firmware);
        toolbarTitle = fragmentView.findViewById(R.id.toolbarDeviceTitle);

        toolbarTitle.setText(deviceID);

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
            public void onResponse(Call<ArrayList<Firmware>> call, retrofit2.Response<ArrayList<Firmware>> response) {

                Log.d(TAG, "response: " + response.message());
                Log.d(TAG, "response: " + response);
                Log.d(TAG, "response: " + response.body());

                int responseCode = response.code();
                switch (responseCode) {
                    case 200:
                        Log.i(TAG, "Fetching data was successful. Number of firmware: " + response.body().size());

                        listOfFirmware = response.body();

                        ArrayList<String> listOfFirmwareString = new ArrayList<>();

                        for (int i = 0; i < listOfFirmware.size(); i++) {
                            listOfFirmwareString.add(listOfFirmware.get(i).getTitle());
                        }

                        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listOfFirmwareString);

                        firmwareListView.setAdapter(adapter);

//                        if (listOfFirmware.size() > 0) {
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
//                        listAdapter = new DocumentsListAdapter(listOfFirmware);
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
            public void onFailure(Call<ArrayList<Firmware>> call, Throwable t) {
                Log.e(TAG, "Get files error" + t.toString());
            }
        });
    }
}
