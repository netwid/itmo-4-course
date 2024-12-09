/* USER CODE BEGIN Header */
/**
  ******************************************************************************
  * @file           : main.c
  * @brief          : Main program body
  ******************************************************************************
  * @attention
  *
  * Copyright (c) 2024 STMicroelectronics.
  * All rights reserved.
  *
  * This software is licensed under terms that can be found in the LICENSE file
  * in the root directory of this software component.
  * If no LICENSE file comes with this software, it is provided AS-IS.
  *
  ******************************************************************************
  */
/* USER CODE END Header */
/* Includes ------------------------------------------------------------------*/
#include "main.h"

/* Private includes ----------------------------------------------------------*/
/* USER CODE BEGIN Includes */

/* USER CODE END Includes */

/* Private typedef -----------------------------------------------------------*/
/* USER CODE BEGIN PTD */

/* USER CODE END PTD */

/* Private define ------------------------------------------------------------*/
/* USER CODE BEGIN PD */

/* USER CODE END PD */

/* Private macro -------------------------------------------------------------*/
/* USER CODE BEGIN PM */

/* USER CODE END PM */

/* Private variables ---------------------------------------------------------*/
TIM_HandleTypeDef htim4;
TIM_HandleTypeDef htim6;

UART_HandleTypeDef huart6;

/* USER CODE BEGIN PV */

/* USER CODE END PV */

/* Private function prototypes -----------------------------------------------*/
void SystemClock_Config(void);
static void MX_GPIO_Init(void);
static void MX_TIM4_Init(void);
static void MX_USART6_UART_Init(void);
static void MX_TIM6_Init(void);
/* USER CODE BEGIN PFP */

/* USER CODE END PFP */

/* Private user code ---------------------------------------------------------*/
/* USER CODE BEGIN 0 */
struct Mode
{
  int greenPulse;
  int greenMod;
  int yellowPulse;
  int yellowMod;
  int redPulse;
  int redMod;
};

int mode_index = 0;
struct Mode modes[] = {
  {
    0, 200,
    100, 200,
    0, 0
  },
  {
    0, 400,
    0, 0,
    200, 400
  },
  {
    0, 600,
    200, 600,
    400, 600
  },
  {
    0, 200,
    0, 200,
    100, 200
  },
  {}
};
size_t modes_length = sizeof(modes) / sizeof(modes[0]);

struct Mode current, custom;

int speed = 5;

void HAL_TIM_PeriodElapsedCallback(TIM_HandleTypeDef *htim) {
  if (htim->Instance == TIM6) {
    current.greenPulse = (current.greenPulse + speed) % current.greenMod;
    current.yellowPulse = (current.yellowPulse + speed) % current.yellowMod;
    current.redPulse = (current.redPulse + speed) % current.redMod;

    if (current.greenPulse > 200) {
      htim4.Instance->CCR2 = 0;
    } else {
      htim4.Instance->CCR2 = current.greenPulse <= 100 ? current.greenPulse : 200 - current.greenPulse;
    }
    if (current.yellowPulse > 200) {
      htim4.Instance->CCR3 = 0;
    } else {
      htim4.Instance->CCR3 = current.yellowPulse <= 100 ? current.yellowPulse : 200 - current.yellowPulse;
    }
    if (current.redPulse > 200) {
      htim4.Instance->CCR4 = 0;
    } else {
      htim4.Instance->CCR4 = current.redPulse <= 100 ? current.redPulse : 200 - current.redPulse;
    }
  }
}

_Bool is_main_menu = 1;
int custom_mode_parameter_num = 0;

void Handler(uint8_t *buf) {
  int32_t receivedNumber = 0;

  if (strlen(buf) == 0) {
    is_main_menu = 0;
    custom_mode_parameter_num = 0;
    sprintf(buf, "Enter green start phase: ");
    HAL_UART_Transmit(&huart6, (uint8_t*)buf, strlen(buf), HAL_MAX_DELAY);
    return;
  }
  receivedNumber = atoi((char*)buf);
  if (!is_main_menu) {
    switch (custom_mode_parameter_num)
    {
    case 0:
      current.greenPulse = receivedNumber;
      sprintf(buf, "Enter green mod: ");
      HAL_UART_Transmit(&huart6, (uint8_t*)buf, strlen(buf), HAL_MAX_DELAY);
      break;
    case 1:
      current.greenMod = receivedNumber;
      sprintf(buf, "Enter yellow start phase: ");
      HAL_UART_Transmit(&huart6, (uint8_t*)buf, strlen(buf), HAL_MAX_DELAY);
      break;
    case 2:
      current.yellowPulse = receivedNumber;
      sprintf(buf, "Enter yellow mod: ");
      HAL_UART_Transmit(&huart6, (uint8_t*)buf, strlen(buf), HAL_MAX_DELAY);
      break;
    case 3:
      current.yellowMod = receivedNumber;
      sprintf(buf, "Enter red start phase: ");
      HAL_UART_Transmit(&huart6, (uint8_t*)buf, strlen(buf), HAL_MAX_DELAY);
      break;
    case 4:
      current.redPulse = receivedNumber;
      sprintf(buf, "Enter red mod: ");
      HAL_UART_Transmit(&huart6, (uint8_t*)buf, strlen(buf), HAL_MAX_DELAY);
      break;
    case 5:
      current.redMod = receivedNumber;
      sprintf(buf, "Custom mode:\nGreen: %d, %d\nYellow: %d, %d\nRed: %d, %d\n",
        current.greenPulse, current.greenMod, current.yellowPulse, current.yellowMod,
        current.redPulse, current.redMod);
      HAL_UART_Transmit(&huart6, (uint8_t*)buf, strlen(buf), HAL_MAX_DELAY);
      custom_mode_parameter_num = 0;
      is_main_menu = 1;
      return;
    }
    custom_mode_parameter_num++;
    return;
  }

  switch (receivedNumber)
  {
  case 1:
    mode_index = (mode_index + 1) % modes_length;
    current = modes[mode_index];
    sprintf(buf, "Current mode: %d\n", mode_index + 1);
    HAL_UART_Transmit(&huart6, (uint8_t*)buf, strlen(buf), HAL_MAX_DELAY);
    break;
  case 2:
    mode_index = (modes_length + mode_index - 1) % modes_length;
    sprintf(buf, "Current mode: %d\n", mode_index + 1);
    HAL_UART_Transmit(&huart6, (uint8_t*)buf, strlen(buf), HAL_MAX_DELAY);
    current = modes[mode_index];
    break;
  case 3:
    speed++;
    if (speed > 10) {
      speed = 10;
    }
    sprintf(buf, "Current speed: %d\n", speed);
    HAL_UART_Transmit(&huart6, (uint8_t*)buf, strlen(buf), HAL_MAX_DELAY);
    break;
  case 4:
    speed--;
    if (speed < 1) {
      speed = 1;
    }
    sprintf(buf, "Current speed: %d\n", speed);
    HAL_UART_Transmit(&huart6, (uint8_t*)buf, strlen(buf), HAL_MAX_DELAY);
    break;
  case 5:
    sprintf(buf, "Custom mode:\nGreen: pulse = %d, mod = %d\nYellow: pulse = %d, mod = %d\nRed: pulse = %d, mod = %d\n", 
      custom.greenPulse, custom.greenMod, custom.yellowPulse, custom.yellowMod, custom.redPulse, custom.redMod);
    HAL_UART_Transmit(&huart6, (uint8_t*)buf, strlen(buf), HAL_MAX_DELAY);
    break;
  default:
    sprintf(buf, "Unknown parameter\n");
    HAL_UART_Transmit(&huart6, (uint8_t*)buf, strlen(buf), HAL_MAX_DELAY);
    break;
  }
}


#define BUFFER_SIZE 150

uint8_t receivedData[BUFFER_SIZE];
uint8_t byte = 0;
uint16_t idx = 0;
/* USER CODE END 0 */

/**
  * @brief  The application entry point.
  * @retval int
  */
int main(void)
{

  /* USER CODE BEGIN 1 */

  /* USER CODE END 1 */

  /* MCU Configuration--------------------------------------------------------*/

  /* Reset of all peripherals, Initializes the Flash interface and the Systick. */
  HAL_Init();

  /* USER CODE BEGIN Init */

  /* USER CODE END Init */

  /* Configure the system clock */
  SystemClock_Config();

  /* USER CODE BEGIN SysInit */

  /* USER CODE END SysInit */

  /* Initialize all configured peripherals */
  MX_GPIO_Init();
  MX_TIM4_Init();
  MX_USART6_UART_Init();
  MX_TIM6_Init();
  /* USER CODE BEGIN 2 */

  /* USER CODE END 2 */

  /* Infinite loop */
  /* USER CODE BEGIN WHILE */
  HAL_TIM_Base_Start_IT(&htim6);
  HAL_TIM_PWM_Start(&htim4, TIM_CHANNEL_2);
  HAL_TIM_PWM_Start(&htim4, TIM_CHANNEL_3);
  HAL_TIM_PWM_Start(&htim4, TIM_CHANNEL_4);

  current = modes[mode_index];
  custom = modes[modes_length - 1];

  while (1)
  {
    if (HAL_UART_Receive(&huart6, &byte, 1, HAL_MAX_DELAY) == HAL_OK) {
      if (byte == '\n' || byte == '\r') {
          receivedData[idx] = '\0';
          
          Handler(receivedData);

          idx = 0;
          memset(receivedData, 0, BUFFER_SIZE);
      } else if (idx < BUFFER_SIZE - 1) {
          receivedData[idx++] = byte;
      }
    }
    /* USER CODE END WHILE */

    /* USER CODE BEGIN 3 */
  }
  /* USER CODE END 3 */
}

/**
  * @brief System Clock Configuration
  * @retval None
  */
void SystemClock_Config(void)
{
  RCC_OscInitTypeDef RCC_OscInitStruct = {0};
  RCC_ClkInitTypeDef RCC_ClkInitStruct = {0};

  /** Configure the main internal regulator output voltage
  */
  __HAL_RCC_PWR_CLK_ENABLE();
  __HAL_PWR_VOLTAGESCALING_CONFIG(PWR_REGULATOR_VOLTAGE_SCALE3);

  /** Initializes the RCC Oscillators according to the specified parameters
  * in the RCC_OscInitTypeDef structure.
  */
  RCC_OscInitStruct.OscillatorType = RCC_OSCILLATORTYPE_HSE;
  RCC_OscInitStruct.HSEState = RCC_HSE_ON;
  RCC_OscInitStruct.PLL.PLLState = RCC_PLL_ON;
  RCC_OscInitStruct.PLL.PLLSource = RCC_PLLSOURCE_HSE;
  RCC_OscInitStruct.PLL.PLLM = 15;
  RCC_OscInitStruct.PLL.PLLN = 108;
  RCC_OscInitStruct.PLL.PLLP = RCC_PLLP_DIV2;
  RCC_OscInitStruct.PLL.PLLQ = 4;
  if (HAL_RCC_OscConfig(&RCC_OscInitStruct) != HAL_OK)
  {
    Error_Handler();
  }

  /** Activate the Over-Drive mode
  */
  if (HAL_PWREx_EnableOverDrive() != HAL_OK)
  {
    Error_Handler();
  }

  /** Initializes the CPU, AHB and APB buses clocks
  */
  RCC_ClkInitStruct.ClockType = RCC_CLOCKTYPE_HCLK|RCC_CLOCKTYPE_SYSCLK
                              |RCC_CLOCKTYPE_PCLK1|RCC_CLOCKTYPE_PCLK2;
  RCC_ClkInitStruct.SYSCLKSource = RCC_SYSCLKSOURCE_PLLCLK;
  RCC_ClkInitStruct.AHBCLKDivider = RCC_SYSCLK_DIV1;
  RCC_ClkInitStruct.APB1CLKDivider = RCC_HCLK_DIV2;
  RCC_ClkInitStruct.APB2CLKDivider = RCC_HCLK_DIV2;

  if (HAL_RCC_ClockConfig(&RCC_ClkInitStruct, FLASH_LATENCY_2) != HAL_OK)
  {
    Error_Handler();
  }
}

/**
  * @brief TIM4 Initialization Function
  * @param None
  * @retval None
  */
static void MX_TIM4_Init(void)
{

  /* USER CODE BEGIN TIM4_Init 0 */

  /* USER CODE END TIM4_Init 0 */

  TIM_MasterConfigTypeDef sMasterConfig = {0};
  TIM_OC_InitTypeDef sConfigOC = {0};

  /* USER CODE BEGIN TIM4_Init 1 */

  /* USER CODE END TIM4_Init 1 */
  htim4.Instance = TIM4;
  htim4.Init.Prescaler = 224;
  htim4.Init.CounterMode = TIM_COUNTERMODE_UP;
  htim4.Init.Period = 99;
  htim4.Init.ClockDivision = TIM_CLOCKDIVISION_DIV1;
  htim4.Init.AutoReloadPreload = TIM_AUTORELOAD_PRELOAD_DISABLE;
  if (HAL_TIM_PWM_Init(&htim4) != HAL_OK)
  {
    Error_Handler();
  }
  sMasterConfig.MasterOutputTrigger = TIM_TRGO_RESET;
  sMasterConfig.MasterSlaveMode = TIM_MASTERSLAVEMODE_DISABLE;
  if (HAL_TIMEx_MasterConfigSynchronization(&htim4, &sMasterConfig) != HAL_OK)
  {
    Error_Handler();
  }
  sConfigOC.OCMode = TIM_OCMODE_PWM1;
  sConfigOC.Pulse = 0;
  sConfigOC.OCPolarity = TIM_OCPOLARITY_HIGH;
  sConfigOC.OCFastMode = TIM_OCFAST_DISABLE;
  if (HAL_TIM_PWM_ConfigChannel(&htim4, &sConfigOC, TIM_CHANNEL_2) != HAL_OK)
  {
    Error_Handler();
  }
  if (HAL_TIM_PWM_ConfigChannel(&htim4, &sConfigOC, TIM_CHANNEL_3) != HAL_OK)
  {
    Error_Handler();
  }
  if (HAL_TIM_PWM_ConfigChannel(&htim4, &sConfigOC, TIM_CHANNEL_4) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN TIM4_Init 2 */

  /* USER CODE END TIM4_Init 2 */
  HAL_TIM_MspPostInit(&htim4);

}

/**
  * @brief TIM6 Initialization Function
  * @param None
  * @retval None
  */
static void MX_TIM6_Init(void)
{

  /* USER CODE BEGIN TIM6_Init 0 */

  /* USER CODE END TIM6_Init 0 */

  TIM_MasterConfigTypeDef sMasterConfig = {0};

  /* USER CODE BEGIN TIM6_Init 1 */

  /* USER CODE END TIM6_Init 1 */
  htim6.Instance = TIM6;
  htim6.Init.Prescaler = 1999;
  htim6.Init.CounterMode = TIM_COUNTERMODE_UP;
  htim6.Init.Period = 999;
  htim6.Init.AutoReloadPreload = TIM_AUTORELOAD_PRELOAD_DISABLE;
  if (HAL_TIM_Base_Init(&htim6) != HAL_OK)
  {
    Error_Handler();
  }
  sMasterConfig.MasterOutputTrigger = TIM_TRGO_RESET;
  sMasterConfig.MasterSlaveMode = TIM_MASTERSLAVEMODE_DISABLE;
  if (HAL_TIMEx_MasterConfigSynchronization(&htim6, &sMasterConfig) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN TIM6_Init 2 */

  /* USER CODE END TIM6_Init 2 */

}

/**
  * @brief USART6 Initialization Function
  * @param None
  * @retval None
  */
static void MX_USART6_UART_Init(void)
{

  /* USER CODE BEGIN USART6_Init 0 */

  /* USER CODE END USART6_Init 0 */

  /* USER CODE BEGIN USART6_Init 1 */

  /* USER CODE END USART6_Init 1 */
  huart6.Instance = USART6;
  huart6.Init.BaudRate = 115200;
  huart6.Init.WordLength = UART_WORDLENGTH_8B;
  huart6.Init.StopBits = UART_STOPBITS_1;
  huart6.Init.Parity = UART_PARITY_NONE;
  huart6.Init.Mode = UART_MODE_TX_RX;
  huart6.Init.HwFlowCtl = UART_HWCONTROL_NONE;
  huart6.Init.OverSampling = UART_OVERSAMPLING_16;
  if (HAL_UART_Init(&huart6) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN USART6_Init 2 */

  /* USER CODE END USART6_Init 2 */

}

/**
  * @brief GPIO Initialization Function
  * @param None
  * @retval None
  */
static void MX_GPIO_Init(void)
{
/* USER CODE BEGIN MX_GPIO_Init_1 */
/* USER CODE END MX_GPIO_Init_1 */

  /* GPIO Ports Clock Enable */
  __HAL_RCC_GPIOH_CLK_ENABLE();
  __HAL_RCC_GPIOD_CLK_ENABLE();
  __HAL_RCC_GPIOC_CLK_ENABLE();

/* USER CODE BEGIN MX_GPIO_Init_2 */
/* USER CODE END MX_GPIO_Init_2 */
}

/* USER CODE BEGIN 4 */

/* USER CODE END 4 */

/**
  * @brief  This function is executed in case of error occurrence.
  * @retval None
  */
void Error_Handler(void)
{
  /* USER CODE BEGIN Error_Handler_Debug */
  /* User can add his own implementation to report the HAL error return state */
  __disable_irq();
  while (1)
  {
  }
  /* USER CODE END Error_Handler_Debug */
}

#ifdef  USE_FULL_ASSERT
/**
  * @brief  Reports the name of the source file and the source line number
  *         where the assert_param error has occurred.
  * @param  file: pointer to the source file name
  * @param  line: assert_param error line source number
  * @retval None
  */
void assert_failed(uint8_t *file, uint32_t line)
{
  /* USER CODE BEGIN 6 */
  /* User can add his own implementation to report the file name and line number,
     ex: printf("Wrong parameters value: file %s on line %d\r\n", file, line) */
  /* USER CODE END 6 */
}
#endif /* USE_FULL_ASSERT */
