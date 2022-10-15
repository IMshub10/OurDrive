package com.summer.ourdrive.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.summer.ourdrive.R
import com.summer.ourdrive.databinding.DialogAddFolderBinding

class AddFolderDialog(private val addFolderDialogInterface: AddFolderDialogInterface?) :
    DialogFragment() {

    interface AddFolderDialogInterface {
        fun save(folderName: String)
    }

    companion object {
        const val TAG = "AddFolderDialog"
        fun newInstance(addFolderDialogInterface: AddFolderDialogInterface): DialogFragment {
            val addFolderDialog = AddFolderDialog(addFolderDialogInterface)
            addFolderDialog.run {
                setStyle(STYLE_NO_FRAME, R.style.alert_dialog)
                isCancelable = false
            }
            return addFolderDialog
        }
    }

    private lateinit var binding: DialogAddFolderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddFolderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners()
    }

    private fun invalidInputForFolderName() {
        binding.tilDiaAddFolderFolderName.error = "Invalid Input"
    }

    private fun listeners() {
        binding.run {
            btnDiaAddFolder.setOnClickListener {
                val folderNameString = etDiaAddFolderFolderName.text
                if (folderNameString == null) {
                    invalidInputForFolderName()
                } else {
                    if (folderNameString.trim().toString().isEmpty()) {
                        invalidInputForFolderName()
                    } else {
                        dismiss()
                        addFolderDialogInterface?.save(folderNameString.trim().toString())
                    }
                }
            }
            btnDiaCancel.setOnClickListener {
                dismiss()
            }
            etDiaAddFolderFolderName.addTextChangedListener {
                tilDiaAddFolderFolderName.error = null
            }
        }

    }
}