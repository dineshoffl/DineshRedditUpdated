package com.dineshredditsample.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dineshredditsample.R;
import com.dineshredditsample.adapter.CustomAdapter;
import com.dineshredditsample.adapter.CustomArrayAdapter;
import com.dineshredditsample.customrecycle.AAH_CustomRecyclerView;
import com.dineshredditsample.helpers.PaginationScrollListener;
import com.dineshredditsample.helpers.ShoppingApplication;
import com.dineshredditsample.helpers.UtilsDefault;
import com.dineshredditsample.models.ModelRedditPopular;
import com.dineshredditsample.models.Spinner_Array;
import com.dineshredditsample.retrofit.API;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by $Dinesh on 12/7/2019.
 */
public class FragmentPopularList extends Fragment {

    ProgressDialog dialog;
    @Inject
    API api;
    AlertDialog alertDialog;

    @BindView(R.id.rv_home)
    AAH_CustomRecyclerView recyclerView;
    String after="";
    List<ModelRedditPopular.Data.Child>list=new ArrayList<>();
    List<Spinner_Array>spinner_arrays=new ArrayList<>();
    CustomAdapter adapter;
    Spinner spinner;
    boolean isLastPage=false;
    boolean isLoading=false;
    int type=0;


    @Override
    public View
    onCreateView(LayoutInflater inflater, ViewGroup container,
                 Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_popular_list, container, false);
        ButterKnife.bind(this, rootView);
        spinner=rootView.findViewById(R.id.spinner);

       // AndroidSupportInjection.inject(this);
        ShoppingApplication.getContext().getComponent().inject(this);

        dialog = new ProgressDialog(getActivity());
        adapter=new CustomAdapter(list,getActivity(),0);
        recyclerView.stopVideos();
        recyclerView.setActivity(getActivity());
        LinearLayoutManager linearLayout=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayout);
        recyclerView.removeAllViewsInLayout();
        // recyclerView.setPlayOnlyFirstVideo(true);
        recyclerView.setAdapter(adapter);
        Spinner_Array array=new Spinner_Array();
        array.setImage(R.drawable.hot);
        array.setName("Hot");
        spinner_arrays.add(array);

        array=new Spinner_Array();
        array.setImage(R.drawable.rise);
        array.setName("Rising");
        spinner_arrays.add(array);
        CustomArrayAdapter adapter=new CustomArrayAdapter(getActivity(),R.layout.list_item_spinner,spinner_arrays);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    type=0;
                    list.clear();
                    adapter.notifyDataSetChanged();
                    isLastPage=false;
                    after="";
                    getPopulars();
                }
                else {
                    type=2;
                    after="";
                    list.clear();
                    isLastPage=false;
                    adapter.notifyDataSetChanged();
                    getRise();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayout) {
            @Override
            protected void loadMoreItems() {
                if (list.size() >= 10) {
                    if (type==0){
                        getPopulars();
                    }
                    else {
                        getRise();
                    }


                }

            }

            @Override
            public int getTotalPageCount() {
                return 0;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        getPopulars();
        return rootView;

    }

    public void shwProgress() {
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);

        dialog.show();
        ProgressBar progressbar = (ProgressBar) dialog.findViewById(android.R.id.progress);
        progressbar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#16172b"), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    public void hideprogress() {


        dialog.hide();

    }

    public void alertShowError(String title) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_login_required, null);
        alertDialogBuilder.setView(dialogView);
        TextView titletv = dialogView.findViewById(R.id.tv_title);

        Button btn_login = dialogView.findViewById(R.id.btn_login);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();

            }
        });


        alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        alertDialog.show();
    }
    public void getRise() {
        if (!UtilsDefault.isOnline()) {
            alertShowError("");
            return;

        }
        shwProgress();
        //  Log.d("aviparms", "search: "+UtilsDefault.getSharedPreferenceValue(Constants.USER_ID)+search);


        api.getrise("10",after).enqueue(new Callback<ModelRedditPopular>() {
            @Override
            public void onResponse(Call<ModelRedditPopular> call, Response<ModelRedditPopular> response) {
                hideprogress();
                isLoading=false;
                if (response.body() != null) {
                    ModelRedditPopular data = response.body();
                    after=data.getData().getAfter();



                    if (data.getData().getChildren() != null && data.getData().getChildren().size() != 0) {
                        list.addAll(data.getData().getChildren());
                        adapter.notifyDataSetChanged();
                        if (list.size()==100){
                            isLastPage=true;
                        }

                        //orderList.clear();
                        // CustomAdapter




                    } else {
                        Toast.makeText(getActivity(), getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ModelRedditPopular> call, Throwable t) {
                hideprogress();
                isLoading=false;
                Log.d("failure", "onFailure: " + t.getMessage());
                Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getPopulars() {
        if (!UtilsDefault.isOnline()) {
            alertShowError("");
            return;

        }
        shwProgress();
        //  Log.d("aviparms", "search: "+UtilsDefault.getSharedPreferenceValue(Constants.USER_ID)+search);


        api.getPopular("10",after).enqueue(new Callback<ModelRedditPopular>() {
            @Override
            public void onResponse(Call<ModelRedditPopular> call, Response<ModelRedditPopular> response) {
                hideprogress();
                if (response.body() != null) {
                    ModelRedditPopular data = response.body();
                    after=data.getData().getAfter();



                    if (data.getData().getChildren() != null && data.getData().getChildren().size() != 0) {
                        list.addAll(data.getData().getChildren());
                        adapter.notifyDataSetChanged();
                        if (list.size()==100){
                            isLastPage=true;
                        }

                        //orderList.clear();
                       // CustomAdapter




                    } else {
                        Toast.makeText(getActivity(), getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ModelRedditPopular> call, Throwable t) {
                hideprogress();
                Log.d("failure", "onFailure: " + t.getMessage());
                Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
