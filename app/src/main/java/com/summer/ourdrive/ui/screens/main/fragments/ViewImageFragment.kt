package com.summer.ourdrive.ui.screens.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.summer.ourdrive.databinding.FragmentViewImageBinding
import com.summer.ourdrive.ui.models.Image
import com.summer.ourdrive.ui.screens.main.MainViewModel
import com.summer.ourdrive.utils.DownloadUtils
import com.summer.ourdrive.utils.FirebaseStorageUtils
import com.summer.ourdrive.utils.Utils
import com.summer.ourdrive.viewmodelFactory.AppViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewImageFragment : Fragment() {

    private lateinit var binding: FragmentViewImageBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var imageId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        arguments?.getString("imageId")?.let {
            imageId = it
        }
    }

    private fun initViewModel() {
        viewModel =
            ViewModelProvider(
                requireActivity(),
                AppViewModelFactory(application = requireActivity().application)
            )[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewImageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            ivItemImageCloudUpload.setOnClickListener {
                lifecycleScope.launch(Dispatchers.Default) {
                    val imageEnt = viewModel.getImageByImageId(imageId)
                    Utils.getUriFromInternalStorage(
                        requireActivity(),
                        imageEnt.folderId,
                        imageEnt.key
                    )
                        ?.let {
                            withContext(Dispatchers.Main) {
                                FirebaseStorageUtils.getImageStorageReference(
                                    imageEnt.folderId,
                                    imageEnt.key
                                ).putFile(it).addOnSuccessListener {
                                    FirebaseStorageUtils.getImageStorageReference(
                                        imageEnt.folderId,
                                        imageEnt.key
                                    ).downloadUrl.addOnSuccessListener { uri ->
                                        lifecycleScope.launch(Dispatchers.Default) {
                                            viewModel.updateImageEntityWithImageUrl(
                                                imageEnt.key,
                                                uri.toString()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                }
            }
            ivItemImageCloudDownload.setOnClickListener {
                lifecycleScope.launch(Dispatchers.Default) {
                    val imageEnt = viewModel.getImageByImageId(imageId)
                    imageEnt.imageUrl?.let {
                        DownloadUtils.downloadImage(requireContext(), it,imageEnt.folderId,imageId)
                    }
                }
            }
        }
        viewModel.getImageByImageIdLive(imageId).observe(viewLifecycleOwner) { imageEnt ->
            binding.run {
                model =
                    Image(
                        imageEnt.key, imageEnt.folderId, imageEnt.imageUrl, null
                    ).also {
                        lifecycleScope.launch {
                            it.bitmap = Utils.getBitmapFromInternalStorage(
                                requireActivity(),
                                imageEnt.folderId,
                                it.id
                            )
                            it.notifyChange()
                        }
                    }
            }
        }
    }

}