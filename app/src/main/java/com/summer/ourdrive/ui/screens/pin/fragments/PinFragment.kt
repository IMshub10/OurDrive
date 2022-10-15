package com.summer.ourdrive.ui.screens.pin.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.andrognito.pinlockview.PinLockListener
import com.summer.ourdrive.databinding.FragmentPinBinding
import com.summer.ourdrive.ui.screens.main.MainActivity
import com.summer.ourdrive.utils.DataUtils
import com.summer.ourdrive.utils.LauncherUtils

class PinFragment : Fragment() {

    private lateinit var binding: FragmentPinBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPinBinding.inflate(inflater)
        binding.fragPinUseLock.attachIndicatorDots(binding.fragPinIndicatorDots)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners()
    }

    private fun listeners() {
        binding.fragPinUseLock.setPinLockListener(object : PinLockListener {
            override fun onComplete(pin: String?) {
                if (pin == null) return
                if (pin.length != 4) return
                if (pin == DataUtils.PIN.toString()) {
                    LauncherUtils.finishAndStartActivity(
                        Intent(
                            requireContext(),
                            MainActivity::class.java
                        ), requireActivity()
                    )
                } else {
                    Toast.makeText(requireContext(), "Invalid Pin", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onEmpty() {

            }

            override fun onPinChange(pinLength: Int, intermediatePin: String?) {

            }

        })
    }
}