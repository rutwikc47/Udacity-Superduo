package it.jaschke.alexandria.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.jaschke.alexandria.MainActivity;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;


public class BookDetail extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EAN_KEY = "EAN";
    private final int LOADER_ID = 10;
    private View rootView;
    private String ean;
    private String bookTitle;
    private ShareActionProvider shareActionProvider;

    public Context mContext;
    private static final String TAG = "BOOK_DETAIL";


    public String bookSubTitle;
    public String desc;
    public String categories;
    public String authors;
    public String imgUrl;
    public String[] authorsArray;


    public String title;
    public String author;
    public String descrip;
    public String categor;
    public String subtitle;
    public String imagurl;
    public String[] autharr;


    public BookDetail(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


/** The View use to expand from the middle giving bad ui/ux experience -Fixed **/

/** The App Crashed on Rotation as it did not retain its state -Fixed **/

        rootView = inflater.inflate(R.layout.fragment_full_books, container, false);

        rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                getActivity().getSupportFragmentManager().popBackStack();

            }
        });

        Bundle arguments = getArguments();
            if (arguments != null) {
                ean = arguments.getString(BookDetail.EAN_KEY);
                if (savedInstanceState == null) {
                    getLoaderManager().restartLoader(LOADER_ID, null, this);
                 }else {
                    title = savedInstanceState.getString("title");
                    subtitle = savedInstanceState.getString("subtitle");
                    author = savedInstanceState.getString("author");
                    descrip = savedInstanceState.getString("descrip");
                    categor = savedInstanceState.getString("categor");
                    imagurl = savedInstanceState.getString("imagurl");
                    autharr = savedInstanceState.getStringArray("autharr");


                    if (title!=null){
                        ((TextView) rootView.findViewById(R.id.fullBookTitle)).setText(title);
                    }else {
                        ((TextView) rootView.findViewById(R.id.fullBookTitle)).setText(R.string.no_info);
                    }

                    if (subtitle!=null){
                        ((TextView) rootView.findViewById(R.id.fullBookSubTitle)).setText(subtitle);
                    }else {
                        ((TextView) rootView.findViewById(R.id.fullBookSubTitle)).setText(R.string.subtitle_na);
                    }

                    if (descrip!=null){
                        ((TextView) rootView.findViewById(R.id.fullBookDesc)).setText(descrip);
                    }else {
                        ((TextView) rootView.findViewById(R.id.fullBookDesc)).setText(R.string.no_info);
                    }

                    if (author!=null){
                        String[] authorsArr = author.split(",");
                        ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
                        ((TextView) rootView.findViewById(R.id.authors)).setText(author.replace(",", "\n"));
                    }else {
                        ((TextView) rootView.findViewById(R.id.authors)).setText(R.string.no_info_author);

                    }

                    if (categor!=null){
                        ((TextView) rootView.findViewById(R.id.categories)).setText(categor);
                    }else {
                        ((TextView) rootView.findViewById(R.id.categories)).setText(R.string.no_info);
                    }

                    if(Patterns.WEB_URL.matcher(imagurl).matches()){
                        new DownloadImage((ImageView) rootView.findViewById(R.id.fullBookCover)).execute(imagurl);
                        rootView.findViewById(R.id.fullBookCover).setVisibility(View.VISIBLE);
                    }

                    if(rootView.findViewById(R.id.right_container)!=null){

                    }
                }
            }
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.book_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(ean)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));

        if (bookTitle!=null){
            ((TextView) rootView.findViewById(R.id.fullBookTitle)).setText(bookTitle);
        }else {
            ((TextView) rootView.findViewById(R.id.fullBookTitle)).setText(R.string.no_info);

        }



        bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));

        if (bookSubTitle!=null){
            ((TextView) rootView.findViewById(R.id.fullBookSubTitle)).setText(bookSubTitle);
        }else {
            ((TextView) rootView.findViewById(R.id.fullBookSubTitle)).setText(R.string.subtitle_na);

        }

        desc = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));

        if (desc!=null){
            ((TextView) rootView.findViewById(R.id.fullBookDesc)).setText(desc);
        }else {
            ((TextView) rootView.findViewById(R.id.fullBookDesc)).setText(R.string.no_info);
        }

        authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        if (authors!=null){
            authorsArray = authors.split(",");
            ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArray.length);
            ((TextView) rootView.findViewById(R.id.authors)).setText(authors.replace(",", "\n"));
        }else {
            ((TextView)rootView.findViewById(R.id.authors)).setText(R.string.no_info_author);
        }

        categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        ((TextView) rootView.findViewById(R.id.categories)).setText(categories);

        if(rootView.findViewById(R.id.right_container)!=null){

        }

        imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
            new DownloadImage((ImageView) rootView.findViewById(R.id.fullBookCover)).execute(imgUrl);
            rootView.findViewById(R.id.fullBookCover).setVisibility(View.VISIBLE);
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text)+bookTitle);
        shareActionProvider.setShareIntent(shareIntent);

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onPause() {
        super.onDestroyView();
        if(MainActivity.IS_TABLET && rootView.findViewById(R.id.right_container) == null){
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.detailsbook);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().setTitle(R.string.books);
    }

    public boolean onBackPressed() {
        int id = R.id.container;
        if(getActivity().findViewById(R.id.right_container) != null){
            id = R.id.right_container;
        }

        ListOfBooks lb=new ListOfBooks();
        getFragmentManager().beginTransaction()
                .replace(id, lb)
                .commit();

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("title", bookTitle);
        outState.putString("subtitle", bookSubTitle);
        outState.putString("categor", categories);
        outState.putString("author", authors);
        outState.putString("descrip", desc);
        outState.putString("imagurl",imgUrl);
        outState.putStringArray("autharr", authorsArray);
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}