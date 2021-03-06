package org.oreon.core.vk.pipeline;

import static org.lwjgl.vulkan.VK10.VK_CULL_MODE_BACK_BIT;
import static org.lwjgl.vulkan.VK10.VK_DYNAMIC_STATE_SCISSOR;
import static org.lwjgl.vulkan.VK10.VK_DYNAMIC_STATE_VIEWPORT;
import static org.lwjgl.vulkan.VK10.VK_FRONT_FACE_CLOCKWISE;
import static org.lwjgl.vulkan.VK10.VK_POLYGON_MODE_FILL;
import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST;
import static org.lwjgl.vulkan.VK10.VK_SAMPLE_COUNT_1_BIT;
import static org.lwjgl.vulkan.VK10.VK_STENCIL_OP_KEEP;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateGraphicsPipelines;
import static org.lwjgl.vulkan.VK10.vkDestroyPipeline;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_R_BIT;
import static org.lwjgl.vulkan.VK10.VK_COMPARE_OP_ALWAYS;
import static org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_G_BIT;
import static org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_B_BIT;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_A_BIT;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkGraphicsPipelineCreateInfo;
import org.lwjgl.vulkan.VkPipelineColorBlendAttachmentState;
import org.lwjgl.vulkan.VkPipelineColorBlendStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineDepthStencilStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineDynamicStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineInputAssemblyStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineMultisampleStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineRasterizationStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineViewportStateCreateInfo;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkViewport;
import org.oreon.core.vk.util.VKUtil;

public class Pipeline {
	
	private VkPipelineVertexInputStateCreateInfo vertexInputState;
	private VkPipelineInputAssemblyStateCreateInfo inputAssembly;
	private VkPipelineViewportStateCreateInfo viewportAndScissorState;
	private VkPipelineRasterizationStateCreateInfo rasterizer;
	private VkPipelineMultisampleStateCreateInfo multisampling;
	private VkPipelineColorBlendStateCreateInfo colorBlending;
	private VkPipelineDepthStencilStateCreateInfo depthStencil;
	private VkPipelineDynamicStateCreateInfo dynamicState;
	private VkViewport.Buffer viewport;
	private VkRect2D.Buffer scissor;
	private IntBuffer pDynamicStates;
	
	private long handle;
	
	private ShaderPipeline shaderPipeline;
	private RenderPass renderPass;
	private PipelineLayout pipelineLayout;
	
	public void createPipeline(VkDevice device, ShaderPipeline shaderPipeline, RenderPass renderPass, PipelineLayout layout){
		
		this.shaderPipeline = shaderPipeline;
		this.renderPass = renderPass;
		this.pipelineLayout = layout;
		
		VkGraphicsPipelineCreateInfo.Buffer pipelineCreateInfo = VkGraphicsPipelineCreateInfo.calloc(1)
				.sType(VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO)
				.pStages(shaderPipeline.getShaderPipeline())
				.pVertexInputState(vertexInputState)
				.pInputAssemblyState(inputAssembly)
				.pViewportState(viewportAndScissorState)
				.pRasterizationState(rasterizer)
				.pMultisampleState(multisampling)
				.pDepthStencilState(null)
				.pColorBlendState(colorBlending)
				.pDynamicState(null)
				.layout(layout.getHandle())
				.renderPass(renderPass.getHandle())
				.subpass(0)
				.basePipelineHandle(VK_NULL_HANDLE)
				.basePipelineIndex(-1);
		
		LongBuffer pPipelines = memAllocLong(1);
		int err = vkCreateGraphicsPipelines(device, VK_NULL_HANDLE, pipelineCreateInfo, null, pPipelines);
		
		handle = pPipelines.get(0);
		
		vertexInputState.free();
		inputAssembly.free();
		viewportAndScissorState.free();
		rasterizer.free();
		multisampling.free();
		colorBlending.free();
		depthStencil.free();
		dynamicState.free();
		viewport.free();
		scissor.free();
		memFree(pDynamicStates);
		shaderPipeline.destroy(device);
		
		if (err != VK_SUCCESS) {
			throw new AssertionError("Failed to create pipeline: " + VKUtil.translateVulkanResult(err));
		}
	}
	
	public void specifyVertexInput(){
		
		vertexInputState = VkPipelineVertexInputStateCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
				.pNext(0)
				.pVertexBindingDescriptions(null)
				.pVertexAttributeDescriptions(null);
	}
	
	public void specifyInputAssembly(){
		
		inputAssembly = VkPipelineInputAssemblyStateCreateInfo.calloc()
		        .sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO)
		        .topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST)
		        .primitiveRestartEnable(false);
	}
	
	public void specifyViewportAndScissor(VkExtent2D extent){
		
		viewport = VkViewport.calloc(1)
				.x(0)
				.y(0)
				.height(extent.height())
		        .width(extent.width())
		        .minDepth(0.0f)
		        .maxDepth(1.0f);
		 
		scissor = VkRect2D.calloc(1);
		scissor.extent().set(extent.width(), extent.height());
		scissor.offset().set(0, 0);
		
		viewportAndScissorState = VkPipelineViewportStateCreateInfo.calloc()
		        .sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO)
		        .viewportCount(1)
		        .pViewports(viewport)
		        .scissorCount(1)
		        .pScissors(scissor);
	}
	
	public void specifyRasterizer(){
		
		rasterizer = VkPipelineRasterizationStateCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO)
				.polygonMode(VK_POLYGON_MODE_FILL)
				.cullMode(VK_CULL_MODE_BACK_BIT)
				.frontFace(VK_FRONT_FACE_CLOCKWISE)
				.rasterizerDiscardEnable(false)
				.lineWidth(1.0f)
				.depthClampEnable(false)
				.depthBiasEnable(false)
				.depthBiasConstantFactor(0)
				.depthBiasSlopeFactor(0)
				.depthBiasClamp(0);
	}
	
	public void specifyMultisampling(){
		
		multisampling = VkPipelineMultisampleStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
                .sampleShadingEnable(false)
                .pSampleMask(null)
                .rasterizationSamples(VK_SAMPLE_COUNT_1_BIT)
                .minSampleShading(1)
                .alphaToCoverageEnable(false)
                .alphaToOneEnable(false);
	}
	
	public void specifyColorBlending(){
	
		VkPipelineColorBlendAttachmentState.Buffer colorWriteMask = VkPipelineColorBlendAttachmentState.calloc(1)
                .blendEnable(false)
                .colorWriteMask(VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT
                				| VK_COLOR_COMPONENT_B_BIT | VK_COLOR_COMPONENT_A_BIT);
        colorBlending = VkPipelineColorBlendStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO)
                .logicOpEnable(false)
                .pAttachments(colorWriteMask);
	}
	
	public void specifyDepthAndStencilTest(){
		
		depthStencil = VkPipelineDepthStencilStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO)
                .depthTestEnable(false)
                .depthWriteEnable(false)
                .depthCompareOp(VK_COMPARE_OP_ALWAYS)
                .depthBoundsTestEnable(false)
                .stencilTestEnable(false);
        depthStencil.back()
                .failOp(VK_STENCIL_OP_KEEP)
                .passOp(VK_STENCIL_OP_KEEP)
                .compareOp(VK_COMPARE_OP_ALWAYS);
        depthStencil.front(depthStencil.back());
	}
	
	public void specifyDynamicState(){
		
		pDynamicStates = memAllocInt(2);
        pDynamicStates.put(VK_DYNAMIC_STATE_VIEWPORT);
        pDynamicStates.put(VK_DYNAMIC_STATE_SCISSOR);
        pDynamicStates.flip();
        
        dynamicState = VkPipelineDynamicStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO)
                .pDynamicStates(pDynamicStates);
	}
	
	public void destroy(VkDevice device){
		
		pipelineLayout.destroy(device);
		renderPass.destroy(device);
		vkDestroyPipeline(device, handle, null);
	}

	public long getHandle() {
		return handle;
	}

	public ShaderPipeline getShaderPipeline() {
		return shaderPipeline;
	}

	public RenderPass getRenderPass() {
		return renderPass;
	}

	public PipelineLayout getPipelineLayout() {
		return pipelineLayout;
	}
	
}
