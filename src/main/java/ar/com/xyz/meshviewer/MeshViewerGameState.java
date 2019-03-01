package ar.com.xyz.meshviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import ar.com.xyz.gameengine.AbstractGameState;
import ar.com.xyz.gameengine.cameracontroller.DefaultCameraController;
import ar.com.xyz.gameengine.collision.Triangle;
import ar.com.xyz.gameengine.entity.spec.EntitySpec;
import ar.com.xyz.gameengine.enumerator.EntityCollisionTypeEnum;
import ar.com.xyz.gameengine.format.obj.OBJWithMaterialModelData;

public class MeshViewerGameState extends AbstractGameState {

	public MeshViewerGameState() {
		/*
		{
			EntitySpec entitySpec ;
			entitySpec = new EntitySpec("box") ;
			entitySpec.setTexture("red.png");
			entitySpec.setRotation(new Vector3f(0, 0, 90)) ;
			entitySpec.setPosition(new Vector3f(0, 0,0)) ;
			entitySpec.setScale(new Vector3f(.5f, .5f, .5f));
//			SimpleDemoEntityController rec = new SimpleDemoEntityController() ;
//			entitySpec.setEntityController(rec);
			entitySpec.setEntityCollisionType(EntityCollisionTypeEnum.SOLID_DYNAMIC);
			createEntity(entitySpec);
//			this.enableDebug(rec.getEntity());
		}
		*/
		
//		{
//			EntitySpec entitySpec ;
//			entitySpec = new EntitySpec("prueba") ;
//			// entitySpec.setTexture("red.png");
//			entitySpec.setColour(new Vector3f(0,1,0));
//			// entitySpec.setRotation(new Vector3f(0, 0, 90)) ;
//			entitySpec.setPosition(new Vector3f(0, 0, 0)) ;
//			// entitySpec.setScale(new Vector3f(.5f, .5f, .5f));
//			entitySpec.setEntityCollisionType(EntityCollisionTypeEnum.SOLID_DYNAMIC);
//			
//			OBJWithMaterialModelData meshWithMaterialModelData = new OBJWithMaterialModelData() ;
//			
//			float vertex[] = {0, 0, 0, 1, 0, 0, 0, 0, 1} ;
//			int index[] = {0, 1, 2} ;
//			meshWithMaterialModelData.add("green.png", vertex /* vertexArray*/, null /* vertexTextureCoordinateArray */, null /* vertexNormalArray */, null /* tangentsArray */, index /* indexArray */);
//			meshWithMaterialModelData.setModelData(vertex /* vertexArray */, null /* vertexTextureCoordinateArray */, null /* vertexNormalArray */, null /* tangentsArray */, index /* indicesArray */);
//			entitySpec.setMeshWithMaterialModelData( meshWithMaterialModelData ) ;
//			createEntity(entitySpec);
//		}
		
		cargarTriangulosDesdeArchivo() ;
		
		grabMouseIfNotGrabbed() ;
		setShowPlayerPosition(true);
	}
	
	@Override
	public void attachedToMainLoop() {
		super.attachedToMainLoop();
		if (getInputManager().getNumberOfConfiguredInputEventListener() == 0) {
			setupPlayerAndCamera() ;
			setupInputEventListeners(getMainGameLoop(), getPlayer(), null) ;
//			addInputEventListener(this);
			
//			if (PLAY) {
//				collisionDataPlayer = new NewDetailedCollisionDataPlayer(this, new ESpaceUtil(new Vector3f(.5f, 1f, .5f))) ;
//				guiControlHelper.setup2dControls(this, this, collisionDataPlayer);
//			} else {
//				SingletonManager.getInstance().getCollisionDataRecorder().setActive(true);
//			}
			
		}
	}
	
	private void setupPlayerAndCamera() {

		setupPlayerAndCamera(
				new Vector3f(-3, 0, 84),// new Vector3f(1, 0, 1),
			new Vector3f(0, 0, 0),
			new Vector3f(1, 1, 1),
			true,
			new Vector3f(.5f, 1f, .5f),
			new Vector3f(.5f, .5f, .5f), null, false,
			null
		) ;

		((DefaultCameraController)getCamera().getCameraController()).decPitch(-90);
		
//		playerDeathHandler = new PlayerDeathHandler(getMainGameLoop(), this) ;
//		getPlayer().setCrushHandler(playerDeathHandler);
		getPlayer().setRunSpeed(1f);
		getPlayer().setStrafeSpeed(1f);
		getPlayer().setGravity(0);
		this.enableDebug(getPlayer());
	}
	
	@Override
	public void tick(float tpf) {
		// TODO Auto-generated method stub
		
	}

	private void cargarTriangulosDesdeArchivo() {
		{
			EntitySpec entitySpec ;
			entitySpec = new EntitySpec("prueba2") ;
			// entitySpec.setTexture("red.png");
			entitySpec.setColour(new Vector3f(0,1,1));
			// entitySpec.setRotation(new Vector3f(0, 0, 90)) ;
			entitySpec.setPosition(new Vector3f(0, 0, 0)) ;
			// entitySpec.setScale(new Vector3f(.5f, .5f, .5f));
			entitySpec.setEntityCollisionType(EntityCollisionTypeEnum.SOLID_DYNAMIC);
			
			// Vector3f[-4.102175, 2.0715857, 85.37304], Vector3f[-0.33926284, 0.04384362, 0.9396693]
			Vector3f origen = new Vector3f(-4.102175f, 2.0715857f, 85.37304f) ;
			Vector3f direccion = new Vector3f(-0.33926284f, 0.04384362f, 0.9396693f) ;
			Vector3f direccionNormalizada = direccion.normalise(null) ;
			
			mostrarEsferas(origen, Vector3f.add(origen, direccionNormalizada, null), Vector3f.add(origen, new Vector3f(direccionNormalizada.x*2,direccionNormalizada.y*2,direccionNormalizada.z*2), null)) ;
			List<Triangle> triangleList = processFile() ;
			
			List<Triangle> triangleAuxList = new ArrayList<Triangle>() ;
			
			for (Triangle triangle :triangleList) {
				System.out.println(triangle.toShortString());
				
				
				if (descartar(triangle, origen.y, origen.y + (direccionNormalizada.y * 2))) {
					continue ;
				}
				
				// rayTracerUtil.rayColidesWithTriangle(origen, direccion, triangle.getP1(), triangle.getP2(), triangle.getP3()) ;
				// Si las normales no estan enfrentadas descartarlo
				
				// Si la altura no esta dentro de un rango descartarlo
				
				triangleAuxList.add(triangle) ;
			}
			
			OBJWithMaterialModelData meshWithMaterialModelData = new OBJWithMaterialModelData() ;
			
//			float vertex[] = {0, 0, 0, 1, 0, 0, 0, 0, 1} ;
//			int index[] = {0, 1, 2} ;
			
			
			float vertex[] = new float[ triangleAuxList.size() * 9 ] ;
			int index[] = new int[ triangleAuxList.size() * 3] ;
			
			int i = 0 ;
			for(Triangle triangle : triangleAuxList) {
				vertex[i*9] = triangle.getP1().x ;
				vertex[i*9+1] = triangle.getP1().y ;
				vertex[i*9+2] = triangle.getP1().z ;
				vertex[i*9+3] = triangle.getP2().x ;
				vertex[i*9+4] = triangle.getP2().y ;
				vertex[i*9+5] = triangle.getP2().z ;
				vertex[i*9+6] = triangle.getP3().x ;
				vertex[i*9+7] = triangle.getP3().y ;
				vertex[i*9+8] = triangle.getP3().z ;
				index[i*3] = i*3 ;
				index[i*3+1] = i*3+1 ;
				index[i*3+2] = i*3+2 ;
				i++ ;
			}

			meshWithMaterialModelData.add("green.png", vertex /* vertexArray*/, null /* vertexTextureCoordinateArray */, null /* vertexNormalArray */, null /* tangentsArray */, index /* indexArray */);
			meshWithMaterialModelData.setModelData(vertex /* vertexArray */, null /* vertexTextureCoordinateArray */, null /* vertexNormalArray */, null /* tangentsArray */, index /* indicesArray */);
			entitySpec.setMeshWithMaterialModelData( meshWithMaterialModelData ) ;
			createEntity(entitySpec);
		}
		
	}
	
	private void mostrarEsferas(Vector3f origin, Vector3f origenMasDireccionNormaliza, Vector3f origenMasDireccionConMagnitud) {
		{
			EntitySpec entitySpec ;
			entitySpec = new EntitySpec("esfera") ;
			entitySpec.setTexture("red.png");
			entitySpec.setPosition(origin) ;
			entitySpec.setScale(new Vector3f(.1f, .1f, .1f));
			entitySpec.setEntityCollisionType(EntityCollisionTypeEnum.NONE);
			createEntity(entitySpec);
//			this.enableDebug(rec.getEntity());
		}
		
		{
			EntitySpec entitySpec ;
			entitySpec = new EntitySpec("esfera") ;
			entitySpec.setTexture("yellow.png");
			entitySpec.setPosition(origenMasDireccionNormaliza) ;
			entitySpec.setScale(new Vector3f(.1f, .1f, .1f));
			entitySpec.setEntityCollisionType(EntityCollisionTypeEnum.NONE);
			createEntity(entitySpec);
//			this.enableDebug(rec.getEntity());
		}
		
		{
			EntitySpec entitySpec ;
			entitySpec = new EntitySpec("esfera") ;
			entitySpec.setTexture("green.png");
			entitySpec.setPosition(origenMasDireccionConMagnitud) ;
			entitySpec.setScale(new Vector3f(.1f, .1f, .1f));
			entitySpec.setEntityCollisionType(EntityCollisionTypeEnum.NONE);
			createEntity(entitySpec);
//			this.enableDebug(rec.getEntity());
		}
	}

	private List<Triangle> processFile() {
		
//		File file = new File(classLoader.getResource(fileName)
//		String file = "problema-colision-zombie-jugador.txt" ;
		BufferedReader br = null;
		InputStreamReader fr = null;
		List<Triangle> triangleList = new ArrayList<Triangle>() ;

		try {

			fr = new InputStreamReader(MeshViewerGameState.class.getClassLoader().getResourceAsStream("problema-colision-zombie-jugador.txt" ));
			br = new BufferedReader(fr);

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
				procesarLinea(sCurrentLine, triangleList) ;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return triangleList ;
	}
	
	private void procesarLinea(String sCurrentLine, List<Triangle> triangleList) {
		// INFO RayTracer getTriangle rayTracerUtil.rayColidesWithTriangle(Vector3f[-4.102175, 2.0715857, 85.37304], Vector3f[-0.33926284, 0.04384362, 0.9396693], Vector3f[-4.3634877, 1.9084129, 85.48128], Vector3f[-4.2435966, 1.6591244, 85.43162], Vector3f[-4.2104144, 1.6591244, 85.48128]) -> false
		String s = sCurrentLine.substring(161) ;
		System.out.println(s);
		int index1 = s.indexOf("]") ;
		String t1 = s.substring(0, index1) ;
		System.out.println(t1);
		s = s.substring(index1 + 12) ;
		System.out.println(s);
		int index2 = s.indexOf("]") ;
		String t2 = s.substring(0, index2) ;
		System.out.println(t2);
		s = s.substring(index2 + 12) ;
		System.out.println(s);
		int index3 = s.indexOf("]") ;
		String t3 = s.substring(0, index3) ;
		System.out.println(t3);
		
		Vector3f p1 = procesarPunto(t1) ;
		Vector3f p2 = procesarPunto(t2) ;
		Vector3f p3 = procesarPunto(t3) ;
		
		triangleList.add(new Triangle(p1, p2, p3)) ;
	}

	private Vector3f procesarPunto(String t) {
		String[] floatValueArray = t.split(",") ;
		return new Vector3f( Float.parseFloat(floatValueArray[0]), Float.parseFloat(floatValueArray[1]), Float.parseFloat(floatValueArray[2])) ;
	}
	
	private boolean descartar(Triangle triangle, float min, float max) {
		if (min(triangle.getP1().y, triangle.getP2().y, triangle.getP3().y) > max) {
			System.out.println("Descartando triangulo por estar arriba");
			return true ;
		}
		if (max(triangle.getP1().y, triangle.getP2().y, triangle.getP3().y) < min) {
			System.out.println("Descartando triangulo por estar debajo");
			return true ;
		}
		return false;
	}

	private float min(float a, float b, float c) {
		if (a < b) {
			if (a < c) {
				return a ;
			} else {
				return c ;
			}
		} else {
			if (b < c) {
				return b ;
			} else {
				return c ;
			}
		}
	}

	private float max(float a, float b, float c) {
		if (a > b) {
			if (a > c) {
				return a ;
			} else {
				return c ;
			}
		} else {
			if (b > c) {
				return b ;
			} else {
				return c ;
			}
		}
	}

}
