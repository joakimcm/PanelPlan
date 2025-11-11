package no.uio.ifi.in2000.joakimcm.solcelleapp

import no.uio.ifi.in2000.joakimcm.solcelleapp.domain.kalkulasjoner.roofAngle
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.angleInput.AngleScreenViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class AngleScreenViewModelTest {

    private lateinit var viewModel: AngleScreenViewModel

    // Initalize viewModel before tests
    @Before
    fun setup() {
        viewModel = AngleScreenViewModel()
    }

    @Test
    fun initializeViewModelResultInEmptyState() {
        //Arrange

        //Act
        val state = viewModel.angleState.value

        //Assert
        assertEquals("", state.height)
        assertEquals("", state.width)
        assertEquals("", state.panelSize)
        assertNull(state.angle)
    }

    @Test
    fun updateHeightInViewModel() {
        //Arrange

        //Act
        viewModel.setHeight("9.0")
        val state = viewModel.angleState.value

        //Assert
        assertEquals("9.0", state.height)

    }


    @Test
    fun setWidthViewModel() {
        //Arrange

        //Act
        viewModel.setWidth("9.0")
        val state = viewModel.angleState.value

        //Assert
        assertEquals("9.0", state.width)

    }

    @Test
    fun setSizeUpdateState() {
        //Arrange

        //Act
        viewModel.setPanelSize("8.0")
        val state = viewModel.angleState.value

        //Assert
        assertEquals("8.0", state.panelSize)
    }

    // Check if calculations are correct
    @Test
    fun angleInStateAfterWidthAndHeight() {

        //Arrange
        val expectedHeight = "8.0"
        val expectedWidth = "9.0"
        val roofAngle = roofAngle(expectedHeight.toDouble(), expectedWidth.toDouble())

        //Act
        viewModel.setHeight(expectedHeight)
        viewModel.setWidth(expectedWidth)
        val state = viewModel.angleState.value

        //Assert
        assertEquals(roofAngle, state.angle)
        assertEquals(expectedHeight, state.height)
        assertEquals(expectedWidth, state.width)
    }


}
