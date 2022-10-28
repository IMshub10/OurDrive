package com.summer.ourdrive.ui.screens.main.fragments

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.summer.ourdrive.database.ImageEntity
import com.summer.ourdrive.databinding.FragmentAddImagesBinding
import com.summer.ourdrive.ui.adapters.ImageAdapter
import com.summer.ourdrive.ui.models.Image
import com.summer.ourdrive.ui.screens.main.MainViewModel
import com.summer.ourdrive.utils.FirebaseStorageUtils
import com.summer.ourdrive.utils.Utils
import com.summer.ourdrive.viewmodelFactory.AppViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class AddImagesFragment : Fragment() {

    companion object {
        const val TAG = "AddImagesFragment"
    }

    private lateinit var binding: FragmentAddImagesBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var folderId: String
    private lateinit var adapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        arguments?.getString("folderId")?.let {
            folderId = it
            lifecycleScope.launch(Dispatchers.IO) { viewModel.fetchLatestImagesFromFolder(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddImagesBinding.inflate(layoutInflater)
        binding.run {
            adapter = ImageAdapter(object : ImageAdapter.ItemClickListener {
                override fun onItemClick(model: Image) {
                    val directions = AddImagesFragmentDirections.addImageToViewImage()
                    directions.imageId = model.id
                    NavHostFragment.findNavController(this@AddImagesFragment)
                        .navigate(directions)
                }

                override fun onUploadClick(model: Image) {
                    lifecycleScope.launch(Dispatchers.Default) {
                        Utils.getUriFromInternalStorage(requireActivity(), model.folderId, model.id)
                            ?.let {
                                withContext(Dispatchers.Main) {
                                    FirebaseStorageUtils.getImageStorageReference(
                                        model.folderId,
                                        model.id
                                    )
                                        .putFile(it).addOnSuccessListener {
                                            FirebaseStorageUtils.getImageStorageReference(
                                                model.folderId,
                                                model.id
                                            ).downloadUrl.addOnSuccessListener { uri ->
                                                lifecycleScope.launch(Dispatchers.IO) {
                                                    viewModel.createANewImageInFolder(
                                                        folderId,
                                                        model.id,
                                                        uri.toString()
                                                    )
                                                    viewModel.updateImageEntityWithImageUrl(
                                                        model.id,
                                                        uri.toString()
                                                    )
                                                }
                                            }
                                        }
                                }
                            }
                    }
                }
            })
            rvFragAddImagesImageList.run {
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                    false
                layoutManager =
                    GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)
                adapter = this@AddImagesFragment.adapter
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        listeners()
    }

    private fun observeViewModel() {
        viewModel.getImagesByFolderId(folderId).observe(viewLifecycleOwner) { list ->
            val imageAdapterList = mutableListOf<Image>()
            list.forEach {
                imageAdapterList.add(Image(
                    it.key, it.folderId, it.imageUrl, null
                ).also {
                    lifecycleScope.launch {
                        it.bitmap = Utils.getBitmapFromInternalStorage(
                            requireActivity(),
                            folderId,
                            it.id
                        )
                        it.notifyChange()
                    }
                })
            }
            adapter.submitList(imageAdapterList)
        }
    }

    private fun listeners() {
        binding.fragAddImagesPickImages.setOnClickListener {
            if (Utils.checkPermissions(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                getImageContent.launch("image/*")
            } else {
                multiplePermissionsLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }

        }
    }

    private val multiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (!Utils.checkPermissions(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                Toast.makeText(
                    requireContext(),
                    "Read Storage Permission is needed to select images",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                getImageContent.launch("image/*")
            }
        }

    private fun initViewModel() {
        viewModel =
            ViewModelProvider(
                requireActivity(),
                AppViewModelFactory(application = requireActivity().application)
            )[MainViewModel::class.java]
    }

    private val getImageContent =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList: List<Uri?> ->
            if (uriList.isEmpty()) return@registerForActivityResult
            binding.pgFragAddImagesProgress.isVisible = true
            CoroutineScope(Dispatchers.Default).launch {
                val imageEntityList = mutableListOf<ImageEntity>()
                uriList.forEach {
                    it?.let { uri ->
                        val imageId = Utils.generateUUID()
                        imageEntityList.add(
                            ImageEntity(
                                imageId,
                                folderId,
                                imageUrl = null
                            )
                        )
                        Utils.saveFileToInternalStorage(requireContext(), folderId, imageId, uri)
                    }
                }
                if (imageEntityList.isNotEmpty()) {
                    viewModel.saveImages(imageEntityList)
                }
                withContext(Dispatchers.Main) {
                    binding.pgFragAddImagesProgress.isVisible = false
                }
            }
        }
}