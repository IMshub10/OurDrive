package com.summer.ourdrive.ui.screens.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.summer.ourdrive.databinding.FragmentMainBinding
import com.summer.ourdrive.dialogs.AddFolderDialog
import com.summer.ourdrive.ui.adapters.FolderAdapter
import com.summer.ourdrive.ui.models.Folder
import com.summer.ourdrive.ui.screens.main.MainViewModel
import com.summer.ourdrive.utils.FirebaseDatabaseUtils
import com.summer.ourdrive.viewmodelFactory.AppViewModelFactory

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: FolderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.run {
            adapter = FolderAdapter(object : FolderAdapter.ItemClickListener {
                override fun onItemClick(model: Folder) {
                    val directions = MainFragmentDirections.mainToAddImages()
                    directions.folderId = model.folderId
                    NavHostFragment.findNavController(this@MainFragment)
                        .navigate(directions)
                }

            })
            rvFragMainFolderList.layoutManager = GridLayoutManager(requireContext(), 2)
            rvFragMainFolderList.adapter = adapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.getAllFolders(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = FirebaseDatabaseUtils.createFolderList(snapshot)
                adapter.submitList(list)
                viewModel.isLoading.set(null)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun listeners() {
        binding.fragMainAddFolder.setOnClickListener {
            if (viewModel.isLoading.get() != null) {
                Toast.makeText(
                    requireContext(),
                    "Wait till all the folders are available.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val addFolderDialog =
                    AddFolderDialog.newInstance(object : AddFolderDialog.AddFolderDialogInterface {
                        override fun save(folderName: String) {
                            viewModel.createANewFolder(folderName)
                        }
                    })
                addFolderDialog.show(childFragmentManager, AddFolderDialog.TAG)
            }
        }
    }

    private fun initViewModel() {
        viewModel =
            ViewModelProvider(
                requireActivity(),
                AppViewModelFactory(application = requireActivity().application)
            )[MainViewModel::class.java]
    }


}