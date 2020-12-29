package com.example.todolist.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.data.ToDo;

import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ToDoViewHolder> {

    private List<ToDo> ToDoList = new ArrayList<>();
    private OnChecked mListener;

    public void setList(List<ToDo> ToDoList) {
        this.ToDoList = ToDoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ToDoViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.li_todo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return ToDoList.size();
    }

    public void setOnCheckedListener(OnChecked mListener) {
        this.mListener = mListener;
    }

    public interface OnChecked {
        void onChecked(ToDo todo);

        void onItemClicked( String title, String description, String date);
    }

    class ToDoViewHolder extends RecyclerView.ViewHolder {

        // Declare your views
        private CheckBox checkBox;
        private TextView li_todo_title;

        public ToDoViewHolder(@NonNull View itemView) {
            super(itemView);
            // inflate the view
            checkBox = itemView.findViewById(R.id.li_todo_check);
            li_todo_title = itemView.findViewById(R.id.li_todo_title);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onChecked(ToDoList.get(getAdapterPosition()));
                }
            });

            li_todo_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClicked(
                            ToDoList.get(getAdapterPosition()).getTitle(),
                            ToDoList.get(getAdapterPosition()).getDescription(),
                            ToDoList.get(getAdapterPosition()).getDate()
                    );
                }
            });

        }

        private void bind(int position) {
            // Bind data
            checkBox.setChecked(ToDoList.get(position).isChecked());
            li_todo_title.setText(ToDoList.get(position).getTitle());

        }
    }
}
