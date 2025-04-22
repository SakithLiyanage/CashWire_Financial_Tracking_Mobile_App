package com.example.cashwire.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cashwire.adapters.CategoryAdapter
import com.example.cashwire.databinding.BottomSheetCategoriesBinding
import com.example.cashwire.models.Category
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CategorySelectionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCategoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter

    var onCategorySelectedListener: ((Category) -> Unit)? = null

    companion object {
        private const val ARG_CATEGORIES = "categories"

        fun newInstance(categories: List<Category>): CategorySelectionBottomSheet {
            val args = Bundle()
            args.putSerializable(ARG_CATEGORIES, ArrayList(categories))
            val fragment = CategorySelectionBottomSheet()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("UNCHECKED_CAST")
        val categories = arguments?.getSerializable(ARG_CATEGORIES) as? ArrayList<Category>
            ?: arrayListOf()

        setupRecyclerView(categories)
    }

    private fun setupRecyclerView(categories: List<Category>) {
        categoryAdapter = CategoryAdapter(categories) {
            onCategorySelectedListener?.invoke(it)
            dismiss()
        }

        binding.recyclerCategories.apply {
            adapter = categoryAdapter
            layoutManager = GridLayoutManager(context, 3)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}