package com.s8.stack.servers.xenon;

import java.io.IOException;

import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.neon.core.NeBranch;
import com.s8.io.bohr.neon.functions.NeSyncFuncGenerator;
import com.s8.io.bohr.neon.functions.arrays.Float32ArrayNeFunction;
import com.s8.io.bohr.neon.functions.arrays.Float64ArrayNeFunction;
import com.s8.io.bohr.neon.functions.arrays.Int16ArrayNeFunction;
import com.s8.io.bohr.neon.functions.arrays.Int32ArrayNeFunction;
import com.s8.io.bohr.neon.functions.arrays.Int64ArrayNeFunction;
import com.s8.io.bohr.neon.functions.arrays.Int8ArrayNeFunction;
import com.s8.io.bohr.neon.functions.arrays.StringUTF8ArrayNeFunction;
import com.s8.io.bohr.neon.functions.arrays.UInt16ArrayNeFunction;
import com.s8.io.bohr.neon.functions.arrays.UInt32ArrayNeFunction;
import com.s8.io.bohr.neon.functions.arrays.UInt64ArrayNeFunction;
import com.s8.io.bohr.neon.functions.arrays.UInt8ArrayNeFunction;
import com.s8.io.bohr.neon.functions.none.VoidNeFunction;
import com.s8.io.bohr.neon.functions.primitives.Bool8NeFunction;
import com.s8.io.bohr.neon.functions.primitives.Float32NeFunction;
import com.s8.io.bohr.neon.functions.primitives.Float64NeFunction;
import com.s8.io.bohr.neon.functions.primitives.Int16NeFunction;
import com.s8.io.bohr.neon.functions.primitives.Int32NeFunction;
import com.s8.io.bohr.neon.functions.primitives.Int64NeFunction;
import com.s8.io.bohr.neon.functions.primitives.Int8NeFunction;
import com.s8.io.bohr.neon.functions.primitives.StringUTF8NeFunction;
import com.s8.io.bohr.neon.functions.primitives.UInt16NeFunction;
import com.s8.io.bohr.neon.functions.primitives.UInt32NeFunction;
import com.s8.io.bohr.neon.functions.primitives.UInt64NeFunction;
import com.s8.io.bohr.neon.functions.primitives.UInt8NeFunction;
import com.s8.io.bohr.neon.lambdas.arrays.Float32ArrayLambda;
import com.s8.io.bohr.neon.lambdas.arrays.Float64ArrayLambda;
import com.s8.io.bohr.neon.lambdas.arrays.Int16ArrayLambda;
import com.s8.io.bohr.neon.lambdas.arrays.Int32ArrayLambda;
import com.s8.io.bohr.neon.lambdas.arrays.Int64ArrayLambda;
import com.s8.io.bohr.neon.lambdas.arrays.Int8ArrayLambda;
import com.s8.io.bohr.neon.lambdas.arrays.StringUTF8ArrayLambda;
import com.s8.io.bohr.neon.lambdas.arrays.UInt16ArrayLambda;
import com.s8.io.bohr.neon.lambdas.arrays.UInt32ArrayLambda;
import com.s8.io.bohr.neon.lambdas.arrays.UInt64ArrayLambda;
import com.s8.io.bohr.neon.lambdas.arrays.UInt8ArrayLambda;
import com.s8.io.bohr.neon.lambdas.none.VoidLambda;
import com.s8.io.bohr.neon.lambdas.primitives.Bool8Lambda;
import com.s8.io.bohr.neon.lambdas.primitives.Float32Lambda;
import com.s8.io.bohr.neon.lambdas.primitives.Float64Lambda;
import com.s8.io.bohr.neon.lambdas.primitives.Int16Lambda;
import com.s8.io.bohr.neon.lambdas.primitives.Int32Lambda;
import com.s8.io.bohr.neon.lambdas.primitives.Int64Lambda;
import com.s8.io.bohr.neon.lambdas.primitives.Int8Lambda;
import com.s8.io.bohr.neon.lambdas.primitives.StringUTF8Lambda;
import com.s8.io.bohr.neon.lambdas.primitives.UInt16Lambda;
import com.s8.io.bohr.neon.lambdas.primitives.UInt32Lambda;
import com.s8.io.bohr.neon.lambdas.primitives.UInt64Lambda;
import com.s8.io.bohr.neon.lambdas.primitives.UInt8Lambda;
import com.s8.io.bytes.linked.LinkedByteOutflow;
import com.s8.stack.arch.helium.http2.HTTP2_Status;
import com.s8.stack.arch.helium.http2.messages.HTTP2_Message;
import com.s8.stack.servers.xenon.tasks.RespondOk;
import com.s8.stack.servers.xenon.tasks.SendError;

public class XeSyncFuncGenerator implements NeSyncFuncGenerator {


	public final SiliconEngine ng;

	public NeBranch branch;


	public XeSyncFuncGenerator(SiliconEngine ng) {
		super();
		this.ng = ng;
	}

	@Override
	public VoidNeFunction createVoidFunc(VoidLambda lambda) {
		return new VoidNeFunction() {

			@Override
			public void run(Object context) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate();

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	
	@Override
	public Bool8NeFunction createBool8Func(Bool8Lambda lambda) {
		return new Bool8NeFunction() {

			@Override
			public void run(Object context, boolean arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}
	
	
	@Override
	public UInt8NeFunction createUInt8Func(UInt8Lambda lambda) {
		return new UInt8NeFunction() {

			@Override
			public void run(Object context, int arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public UInt8ArrayNeFunction createUint8ArrayFunc(UInt8ArrayLambda lambda) {
		return new UInt8ArrayNeFunction() {

			@Override
			public void run(Object context, int[] arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public UInt16NeFunction createUint16Func(UInt16Lambda lambda) {
		return new UInt16NeFunction() {

			@Override
			public void run(Object context, int arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public UInt16ArrayNeFunction createUint16ArrayFunc(UInt16ArrayLambda lambda) {
		return new UInt16ArrayNeFunction() {

			@Override
			public void run(Object context, int[] arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public UInt32NeFunction createUint32Func(UInt32Lambda lambda) {
		return new UInt32NeFunction() {

			@Override
			public void run(Object context, int arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public UInt32ArrayNeFunction createUint32Func(UInt32ArrayLambda lambda) {
		return new UInt32ArrayNeFunction() {

			@Override
			public void run(Object context, int[] arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public UInt64NeFunction createUint64Func(UInt64Lambda lambda) {
		return new UInt64NeFunction() {

			@Override
			public void run(Object context, long arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public UInt64ArrayNeFunction createUint64Func(UInt64ArrayLambda lambda) {
		return new UInt64ArrayNeFunction() {

			@Override
			public void run(Object context, long[] arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public Int8NeFunction createInt8Func(Int8Lambda lambda) {
		return new Int8NeFunction() {

			@Override
			public void run(Object context, int arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public Int8ArrayNeFunction createInt8ArrayFunc(Int8ArrayLambda lambda) {
		return new Int8ArrayNeFunction() {

			@Override
			public void run(Object context, int[] arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public Int16NeFunction createInt16Func(Int16Lambda lambda) {
		return new Int16NeFunction() {

			@Override
			public void run(Object context, int arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public Int16ArrayNeFunction createInt16ArrayFunc(Int16ArrayLambda lambda) {
		return new Int16ArrayNeFunction() {

			@Override
			public void run(Object context, int[] arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public Int32NeFunction createInt32Func(Int32Lambda lambda) {
		return new Int32NeFunction() {

			@Override
			public void run(Object context, int arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public Int32ArrayNeFunction createInt32ArrayFunc(Int32ArrayLambda lambda) {
		return new Int32ArrayNeFunction() {

			@Override
			public void run(Object context, int[] arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public Int64NeFunction createInt64Func(Int64Lambda lambda) {
		return new Int64NeFunction() {

			@Override
			public void run(Object context, long arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public Int64ArrayNeFunction createInt64ArrayFunc(Int64ArrayLambda lambda) {
		return new Int64ArrayNeFunction() {

			@Override
			public void operate(Object context, long[] arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public Float32NeFunction createFloat32Func(Float32Lambda lambda) {
		return new Float32NeFunction() {

			@Override
			public void run(Object context, float arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public Float32ArrayNeFunction createFloat32ArrayFunc(Float32ArrayLambda lambda) {
		return new Float32ArrayNeFunction() {

			@Override
			public void run(Object context, float[] arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public Float64NeFunction createFloat64Func(Float64Lambda lambda) {
		return new Float64NeFunction() {

			@Override
			public void run(Object context, double arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public Float64ArrayNeFunction createFloat64ArrayFunc(Float64ArrayLambda lambda) {
		return new Float64ArrayNeFunction() {

			@Override
			public void run(Object context, double[] arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public StringUTF8NeFunction createStringUTF8Func(StringUTF8Lambda lambda) {
		return new StringUTF8NeFunction() {

			@Override
			public void run(Object context, String arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

	@Override
	public StringUTF8ArrayNeFunction createStringUTF8ArrayFunc(StringUTF8ArrayLambda lambda) {
		return new StringUTF8ArrayNeFunction() {

			@Override
			public void run(Object context, String[] arg) {
				HTTP2_Message response = ((XeContext) context).response;
				try {
					// run lambda
					lambda.operate(arg);

					// publish response
					LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
					branch.outbound.publish(outflow);
					ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
				} 
				catch (IOException e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
				}
				catch (Exception e) {
					e.printStackTrace();
					ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
				}
			}
		};
	}

}
