package com.major.potholerectifier.adapter;

import com.major.potholerectifier.R;
import com.major.potholerectifier.fragment.PotHoleFragment;
import com.major.potholerectifier.model.PotHole;
import com.major.potholerectifier.model.PotHoleStatus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter
        extends FragmentPagerAdapter {
    private List<PotHole> potholes;

    public ViewPagerAdapter(
            @NonNull FragmentManager fm)
    {
        super(fm);
    }


    public ViewPagerAdapter(
            @NonNull FragmentManager fm,List<PotHole> potholes)

    {
        super(fm);
        this.potholes = potholes;
    }
    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        Fragment fragment = null;

        if (position == 0) {
            List<PotHole> openPotHoles = new ArrayList<>();
            for(PotHole p : potholes){
                if(p.getStatus().equals(PotHoleStatus.OPEN)){
                    openPotHoles.add(p);
                }
            }

            fragment = new PotHoleFragment(openPotHoles);
        }else if (position == 1) {
            List<PotHole> inProgressPotHoles = new ArrayList<>();
            for(PotHole p : potholes){
                if(p.getStatus().equals(PotHoleStatus.INPROGRESS)){
                    inProgressPotHoles.add(p);
                }
            }
            fragment = new PotHoleFragment(inProgressPotHoles);
        }else if (position == 2) {
            List<PotHole> completedPotHoles = new ArrayList<>();
            for(PotHole p : potholes){
                if(p.getStatus().equals(PotHoleStatus.COMPLETED)){
                    completedPotHoles.add(p);
                }
            } fragment = new PotHoleFragment(completedPotHoles);
        }
        return fragment;
    }

    @Override
    public int getCount()
    {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        String title = null;
        if (position == 0)
            title = "Open";
        else if (position == 1)
            title = "In Progress";
        else if (position == 2)
            title = "Completed";
        return title;
    }
}