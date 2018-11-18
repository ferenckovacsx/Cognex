package ferenckovacsx.cognex.ui;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

import ferenckovacsx.cognex.api.APIClient;
import ferenckovacsx.cognex.api.APIInterface;
import ferenckovacsx.cognex.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirmwareDescriptionFragment extends Fragment {

    private String description;
    private String title;
    private String created;
    private String version;
    private String filetype;
    private String file;
    private String channel;
    private int id;

    APIInterface apiInterface;

    TextView descriptionTextView, titleTextView, createdTextView, versionTextView, filetypeTextView, fileTextView, channelTextView, idTextView;
    Button downloadButton;
    ImageView backButton;

    final String TAG = "Firmware Description";

    ProgressDialog progressDialog;
    long downloadedsize, totalsize;

    private OnFragmentInteractionListener mListener;



    public FirmwareDescriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            description = getArguments().getString("description");
            title = getArguments().getString("title");
            created = getArguments().getString("created");
            version = getArguments().getString("version");
            filetype = getArguments().getString("filetype");
            file = getArguments().getString("file");
            channel = getArguments().getString("channel");
            id = getArguments().getInt("id");
        }

        //retrofit
        APIClient apiClient = new APIClient(getActivity());
        apiInterface = apiClient.getClient().create(APIInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_firmware_description, container, false);
        descriptionTextView = fragmentView.findViewById(R.id.descriptionTextView);
        titleTextView = fragmentView.findViewById(R.id.toolbarFirmwareTitle);
        createdTextView = fragmentView.findViewById(R.id.createdTextView);
        versionTextView = fragmentView.findViewById(R.id.versionTextView);
        filetypeTextView = fragmentView.findViewById(R.id.filetypeTextView);
        fileTextView = fragmentView.findViewById(R.id.fileTextView);
        channelTextView = fragmentView.findViewById(R.id.channelTextView);
        idTextView = fragmentView.findViewById(R.id.idTextView);
        backButton = fragmentView.findViewById(R.id.firmwareDescriptionBackButton);
        downloadButton = fragmentView.findViewById(R.id.downloadButton);

        descriptionTextView.setText(description);
        titleTextView.setText(title);
        createdTextView.setText(created);
        versionTextView.setText(version);
        filetypeTextView.setText(filetype);
        fileTextView.setText(file);
        channelTextView.setText(channel);
        idTextView.setText(String.valueOf(id));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    downloadFile(file);
                }
            }
        });

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

    void downloadFile(String filename) {



        Call<ResponseBody> call = apiInterface.downloadFileWithFixedUrl(filename);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                if (response.code() == 200) {
                    Log.d(TAG, "server contacted and has file");

                    totalsize = response.body().contentLength();

                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Downloading...");
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            boolean writtenToDisk = writeResponseBodyToDisk(response.body());

                            Log.d(TAG, "file download was a success? " + writtenToDisk);
                            return null;
                        }

                    }.execute();
                } else {
                    Log.d(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "error");
            }
        });

    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {

        Log.d(TAG, "writeToDisk body: " + body);
        try {

            File firmwareFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + file.substring(6));

            Log.d(TAG, "filename: " + firmwareFile.getAbsolutePath());

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                totalsize = body.contentLength();
                downloadedsize = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(firmwareFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    downloadedsize += read;

                    Runnable changeMessage = new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setMessage("Downloading " + formatFileSize(downloadedsize) + " of " + formatFileSize(totalsize));
                        }
                    };

                    getActivity().runOnUiThread(changeMessage);

                    Log.d(TAG, "file download: " + downloadedsize + " of " + totalsize);
                }

                outputStream.flush();

                progressDialog.cancel();

                //open downloads folder
                startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "IO Error", Toast.LENGTH_SHORT).show();
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        Log.d(TAG, "onRequestPermissionResult");
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadFile(file);
                } else {
                    Toast.makeText(getActivity(), "Permission was not granted.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    static String formatFileSize(long size) {
        String hrSize = null;

        double b = size;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.0");

        if (t > 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }
}
