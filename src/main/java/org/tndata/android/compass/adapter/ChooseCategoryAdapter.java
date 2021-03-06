package org.tndata.android.compass.adapter;

import java.util.ArrayList;
import java.util.List;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Category;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Adapter for the grid in the category chooser.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ChooseCategoryAdapter
        extends RecyclerView.Adapter
        implements Animation.AnimationListener{

    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_CATEGORY = 2;
    private static final int VIEW_TYPE_NEXT = 3;


    private final Context mContext;
    private final OnCategoriesSelectedListener mCallback;
    private final boolean mApplyRestrictions;

    private List<Category> mCategories;
    private List<Category> mSelectedCategories;

    private int mLastAnimation;
    private int mCurrentAnimations;

    private boolean mHideProgressBar;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     * @param callback the callback interface.
     * @param applyRestrictions whether the 3 to 5 vtegory restriction should be applied.
     */
    public ChooseCategoryAdapter(Context context, OnCategoriesSelectedListener callback,
                                 boolean applyRestrictions){
        mContext = context;
        mCallback = callback;
        mApplyRestrictions = applyRestrictions;

        mCategories = null;

        mLastAnimation = 0;
        mCurrentAnimations = 0;

        mHideProgressBar = false;
    }

    /**
     * Makes the progress bar go away in the header view.
     */
    public void hideProgressBar(){
        mHideProgressBar = true;
        notifyItemChanged(0);
    }

    /**
     * Sets the dataset of the adapter.
     *
     * @param all the list of all available categories.
     */
    public void setCategories(@NonNull List<Category> all, @NonNull List<Category> selected){
        //Let the GC take care of the previous list and fill a new one
        mCategories = new ArrayList<>();
        mCategories.addAll(all);

        mSelectedCategories = new ArrayList<>();
        mSelectedCategories.addAll(selected);

        notifyDataSetChanged();
    }

    /**
     * Returns the Category at the specified position.
     *
     * @param position the position of the category in the dataset.
     * @return the Category in the requested position.
     */
    public Category getItem(int position){
        return mCategories.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == VIEW_TYPE_HEADER){
            View root = inflater.inflate(R.layout.item_choose_categories_header, parent, false);
            return new HeaderViewHolder(root);
        }
        else if (viewType == VIEW_TYPE_NEXT){
            View root = inflater.inflate(R.layout.item_choose_categories_next, parent, false);
            return new NextViewHolder(root);
        }
        else if (viewType == VIEW_TYPE_CATEGORY){
            View root = inflater.inflate(R.layout.item_choose_categories_category, parent, false);
            return new CategoryViewHolder(root);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        if (getItemViewType(position) == VIEW_TYPE_HEADER){
            HeaderViewHolder holder = (HeaderViewHolder)rawHolder;
            StaggeredGridLayoutManager.LayoutParams params;
            params = (StaggeredGridLayoutManager.LayoutParams)holder.itemView.getLayoutParams();
            params.setFullSpan(true);

            if (mCategories != null || mHideProgressBar){
                holder.progressBar.setVisibility(View.GONE);
            }
        }
        else if (getItemViewType(position) == VIEW_TYPE_NEXT){
            NextViewHolder holder = (NextViewHolder)rawHolder;
            holder.itemView.setVisibility(View.GONE);
            StaggeredGridLayoutManager.LayoutParams params;
            params = (StaggeredGridLayoutManager.LayoutParams)holder.itemView.getLayoutParams();
            params.setFullSpan(true);
            setAnimation(holder, position);
        }
        else if (getItemViewType(position) == VIEW_TYPE_CATEGORY){
            CategoryViewHolder holder = (CategoryViewHolder)rawHolder;
            Category category = getItem(position-1);
            holder.itemView.setVisibility(View.GONE);
            if (!mSelectedCategories.contains(category)){
                holder.mOverlay.setVisibility(View.VISIBLE);
            }
            else{
                holder.mOverlay.setVisibility(View.GONE);
            }

            if (category.getTitle().equalsIgnoreCase("Happiness")){
                holder.mBackground.setImageResource(R.drawable.tile_happiness);
            }
            else if (category.getTitle().equalsIgnoreCase("Community")){
                holder.mBackground.setImageResource(R.drawable.tile_community);
            }
            else if (category.getTitle().equalsIgnoreCase("Family")){
                holder.mBackground.setImageResource(R.drawable.tile_family);
            }
            else if (category.getTitle().equalsIgnoreCase("Home")){
                holder.mBackground.setImageResource(R.drawable.tile_home);
            }
            else if (category.getTitle().equalsIgnoreCase("Romance")){
                holder.mBackground.setImageResource(R.drawable.tile_romance);
            }
            else if (category.getTitle().equalsIgnoreCase("Health")){
                holder.mBackground.setImageResource(R.drawable.tile_health);
            }
            else if (category.getTitle().equalsIgnoreCase("Wellness")){
                holder.mBackground.setImageResource(R.drawable.tile_wellness);
            }
            else if (category.getTitle().equalsIgnoreCase("Safety")){
                holder.mBackground.setImageResource(R.drawable.tile_safety);
            }
            else if (category.getTitle().equalsIgnoreCase("Parenting")){
                holder.mBackground.setImageResource(R.drawable.tile_parenting);
            }
            else if (category.getTitle().equalsIgnoreCase("Education")){
                holder.mBackground.setImageResource(R.drawable.tile_education);
            }
            else if (category.getTitle().equalsIgnoreCase("Skills")){
                holder.mBackground.setImageResource(R.drawable.tile_skills);
            }
            else if (category.getTitle().equalsIgnoreCase("Work")){
                holder.mBackground.setImageResource(R.drawable.tile_work);
            }
            else if (category.getTitle().equalsIgnoreCase("Prosperity")){
                holder.mBackground.setImageResource(R.drawable.tile_prosperity);
            }
            else if (category.getTitle().equalsIgnoreCase("Fun")){
                holder.mBackground.setImageResource(R.drawable.tile_fun);
            }
            else{
                holder.mBackground.setImageResource(0);
            }

            holder.mCaption.setText(category.getTitle());
            setAnimation(holder, position);
        }
    }

    /**
     * Sets an in-animation to a tile if it hasn't been animated already. If there are
     * scheduled animations, it sets it after the last one.
     *
     * @param rawHolder the holder containing the tile to animate in.
     * @param position the position of the tile.
     */
    private void setAnimation(RecyclerView.ViewHolder rawHolder, int position){
        // If the bound tile wasn't previously displayed on screen, it's animated
        if (position > mLastAnimation){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.category_in);
            animation.setAnimationListener(this);
            animation.setStartOffset(100 * mCurrentAnimations);
            mCurrentAnimations++;
            rawHolder.itemView.startAnimation(animation);
            rawHolder.itemView.setVisibility(View.VISIBLE);
            mLastAnimation = position;
        }
        else{
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.category_set);
            rawHolder.itemView.startAnimation(animation);
            rawHolder.itemView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount(){
        //If the categories haven't been set, yet, display the header, otherwise, display
        //  the header, the categories, and the next button.
        return mCategories == null ? 1 : mCategories.size()+2;
    }

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return VIEW_TYPE_HEADER;
        }
        else if (position == getItemCount()-1){
            return VIEW_TYPE_NEXT;
        }
        else{
            return VIEW_TYPE_CATEGORY;
        }
    }

    @Override
    public void onAnimationStart(Animation animation){
        //Unused
    }

    @Override
    public void onAnimationEnd(Animation animation){
        mCurrentAnimations--;
    }

    @Override
    public void onAnimationRepeat(Animation animation){
        //Unused
    }


    /**
     * The grid's header ViewHolder.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class HeaderViewHolder extends RecyclerView.ViewHolder{
        private ProgressBar progressBar;

        /**
         * Constructor.
         *
         * @param itemView the root view of the cell.
         */
        public HeaderViewHolder(View itemView){
            super(itemView);

            progressBar = (ProgressBar)itemView.findViewById(R.id.choose_categories_header_progress_bar);
        }
    }


    /**
     * Holder for a category tile. Handles clicks and animations.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, Animation.AnimationListener{
        private ImageView mBackground;
        private View mOverlay;
        private TextView mCaption;


        /**
         * Constructor.
         *
         * @param itemView the root view of the cell.
         */
        public CategoryViewHolder(View itemView){
            super(itemView);

            //UI components
            mBackground = (ImageView)itemView.findViewById(R.id.choose_categories_category_tile);
            mOverlay = itemView.findViewById(R.id.choose_categories_category_overlay);
            mCaption = (TextView)itemView.findViewById(R.id.choose_categories_category_caption);

            //Listeners
            itemView.setOnClickListener(this);
            itemView.findViewById(R.id.choose_categories_category_more_info).setOnClickListener(this);
        }

        @Override
        public void onClick(final View view){
            //In any event the Category is needed, so it is fetched
            Category category = getItem(getAdapterPosition()-1);

            //If the tile was clicked
            if (view == itemView){
                AlphaAnimation animation;
                //If the category was selected, remove it and fade in the overlay
                if (mSelectedCategories.contains(category)){
                    mSelectedCategories.remove(category);
                    animation = new AlphaAnimation(0, 1);
                }
                //Otherwise add it and fade out the overlay
                else{
                    mSelectedCategories.add(category);
                    animation = new AlphaAnimation(1, 0);
                }
                //Start the animation
                animation.setDuration(200);
                animation.setAnimationListener(this);
                mOverlay.startAnimation(animation);

                //Notify the adapter the item was changed. Sometimes the animation won't fire
                //  if this is not done.
                notifyItemChanged(getLayoutPosition());
            }
            //If the description was clicked
            else{
                //Create a dialog with the description
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(category.getDescription()).setTitle(category.getTitle());
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }
        }

        @Override
        public void onAnimationStart(Animation animation){
            //In any event, the overlay is made visible at the beginning of the animation, It
            //  will be already visible if the category is not selected, which won't have any
            //  effect, but will make it visible if the category is visible.
            mOverlay.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation){
            //When the category has been selected, make the overlay gone, otherwise, when the
            //  animation ends, the overlay will restore to its original alpha state.
            if (getLayoutPosition() != -1){
                if (mSelectedCategories.contains(getItem(getLayoutPosition()-1))){
                    mOverlay.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation){
            //Unused
        }
    }


    /**
     * Holder for the next button tile. Handles clicks.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class NextViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public NextViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            if (mApplyRestrictions){
                if (mSelectedCategories.size() < 3){
                    Toast.makeText(mContext, R.string.choose_categories_at_least_three,
                            Toast.LENGTH_SHORT).show();
                }
                else if (mSelectedCategories.size() > 5){
                    Toast.makeText(mContext, R.string.choose_categories_at_most_five,
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    mCallback.onCategoriesSelected(mSelectedCategories);
                }
            }
            else{
                mCallback.onCategoriesSelected(mSelectedCategories);
            }
        }
    }


    /**
     * Listener for save events.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface OnCategoriesSelectedListener{
        /**
         * Called when the next button is clicked if the conditions are right.
         *
         * @param selection the list of selected categories.
         */
        void onCategoriesSelected(List<Category> selection);
    }
}
